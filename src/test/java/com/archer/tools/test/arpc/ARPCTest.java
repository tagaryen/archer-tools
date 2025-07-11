package com.archer.tools.test.arpc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.archer.net.ChannelContext;
import com.archer.net.ssl.ProtocolVersion;
import com.archer.net.ssl.SslContext;
import com.archer.tools.arpc.*;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONStatic;

public class ARPCTest {
	public static void test() {
		ARPCServer server = new ARPCServer("127.0.0.1", 9612);
		ARPCClient client0 = new ARPCClient("127.0.0.1", 9612);
		ARPCClient client1 = new ARPCClient("127.0.0.1", 9612);
		
		server.addMessageListenner("/你好-徐熠-1", new ARPCMessageListenner<MessageB>() {

			@Override
			public Object onMessage(MessageB in) {
				System.out.println("服务端收到数据B:" + in.getB());
				return new MessageC();
			}});
		server.addMessageListenner("/你好-徐熠", new ARPCMessageListenner<MessageA>() {
			@Override
			public Object onMessage(MessageA in) {
				System.out.println("服务端收到数据A:" + in.getA());
				return new MessageB();
			}});
		
		server.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		client0.callAsync("/你好-徐熠", new MessageA(), new ARPCClientCallback<MessageB>() {
			@Override
			public void onReceive(MessageB r) {
				System.out.println("客户端0收到数据B:" + r.getB());
			}});
		
		MessageC c = client1.call("/你好-徐熠-1", new MessageB(), MessageC.class);
		System.out.println("客户端1收到数据c:" + c.getC());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client0.close();
		client1.close();
		server.close();
	}
	
	public static void serverGmsslTest() {
		SslContext sslctx = new SslContext(true, ProtocolVersion.TLS1_3_VERSION, ProtocolVersion.TLS1_1_VERSION);
		try {
			String basePath = "D:\\projects\\javaProject\\maven\\archer-net\\crt\\gm_cert\\";
			sslctx.trustCertificateAuth(Files.readAllBytes(Paths.get(basePath + "ca.crt")));
			sslctx.useCertificate(Files.readAllBytes(Paths.get(basePath + "server.crt")), Files.readAllBytes(Paths.get(basePath + "server.key")));
			sslctx.useEncryptCertificate(Files.readAllBytes(Paths.get(basePath + "server_en.crt")), Files.readAllBytes(Paths.get(basePath + "server_en.key")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ARPCServer server = new ARPCServer("127.0.0.1", 9607, sslctx);

		server.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void clientGmsslTest() {
		SslContext sslctx = new SslContext(true);
		sslctx.validateHostname("xuyi_node");
		try {
			String basePath = "D:\\projects\\javaProject\\maven\\archer-net\\crt\\gm_cert\\";
			sslctx.trustCertificateAuth(Files.readAllBytes(Paths.get(basePath + "ca.crt")));
			sslctx.useCertificate(Files.readAllBytes(Paths.get(basePath + "cli.crt")), Files.readAllBytes(Paths.get(basePath + "cli.key")));
			sslctx.useEncryptCertificate(Files.readAllBytes(Paths.get(basePath + "cli_en.crt")), Files.readAllBytes(Paths.get(basePath + "cli_en.key")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ARPCClient client = new ARPCClient("127.0.0.1", 9067, sslctx);
		MessageC recv = client.call("/你好", new MessageA(), MessageC.class);
		System.out.println("recv c = " + XJSONStatic.stringify(recv));
	}
	
	public static void main(String args[]) {
//		test();
//		serverGmsslTest();
		clientGmsslTest();
	}
}
