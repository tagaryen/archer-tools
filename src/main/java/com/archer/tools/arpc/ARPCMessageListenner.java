package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.xjson.XJSON;

public abstract class ARPCMessageListenner<Send, Recv> {

	protected XJSON xjson;
	
	private Class<Send> sendCls;
	
	private Class<Recv> recvCls;
	
	protected ARPCMessageListenner(Class<Send> sendCls, Class<Recv> recvCls) {
		this.sendCls = sendCls;
		this.recvCls = recvCls;
		this.xjson = new XJSON();
	}
	
	protected Class<Send> getSendClass() {
		return sendCls;
	}
	
	protected Class<Recv> getRecvClass() {
		return recvCls;
	}
	
	protected void handleMessage(ChannelContext ctx, byte[] seq, Bytes data) {
		Send send = onReceiveAndGenerateSend(ctx, xjson.parse(new String(data.readAll(), StandardCharsets.UTF_8), getRecvClass()));
		if(send == null) {
			return ;
		}
		byte[] name = send.getClass().getSimpleName().toLowerCase().getBytes(StandardCharsets.UTF_8);
		Bytes output = new Bytes();
		output.write(seq);
		output.writeInt16(name.length);
		output.write(name);
		output.write(xjson.stringify(send).getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(output);
	}
	
	/**
	 * calls when a ReceiveMessage{@code (Recv.class)} was send by remote
	 * then generate a SendMessage{@code (Send.class)}, return null to avoid send message back if necessary
	 * @param ctx ChannelContext
	 * @param recv Received message
	 * */
	public abstract Send onReceiveAndGenerateSend(ChannelContext ctx, Recv recv);
	
}
