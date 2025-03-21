package com.archer.tools.test.arpc;

import com.archer.tools.arpc.ARPCClient;
import com.archer.tools.arpc.ARPCClientCallback;
import com.archer.tools.arpc.ARPCClientListenner;
import com.archer.tools.arpc.ARPCServer;
import com.archer.tools.arpc.ARPCServerListenner;
import com.archer.xjson.XJSONStatic;

public class ARPCTest {
	public static void test() {
		ARPCServer server = new ARPCServer("127.0.0.1", 9612);
		ARPCClient client = new ARPCClient("127.0.0.1", 9612);
		
		server.registerListener(new ARPCServerListenner<MessageB, MessageC>(MessageB.class, MessageC.class) {
			@Override
			public MessageC onMessage(MessageB p) {
				System.out.println("receive client messageB " + XJSONStatic.stringify(p));
				return new MessageC();
		}});
		server.registerListener(new ARPCServerListenner<MessageA, MessageB>(MessageA.class, MessageB.class) {
			@Override
			public MessageB onMessage(MessageA p) {
				System.out.println("receive client messageA " + XJSONStatic.stringify(p));
				return new MessageB();
		}});
		
		server.start();
		
		client.registerListener(new ARPCClientListenner<MessageB, MessageC>(MessageB.class, MessageC.class));
		client.registerListener(new ARPCClientListenner<MessageA, MessageB>(MessageA.class, MessageB.class));
		
		client.callRemoteAsync(new MessageB(), new ARPCClientCallback<MessageC>() {
			@Override
			public void onReturn(MessageC r) {
				System.out.println("client receive MessageC " + XJSONStatic.stringify(r));
			}});
		MessageB b = client.callRemote(new MessageA(), MessageB.class);
		System.out.println("client receive MessageB " + XJSONStatic.stringify(b));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client.close();
		server.close();
	}
	
	public static void main(String args[]) {
		
		test();
	}
}
