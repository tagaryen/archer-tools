package com.archer.tools.test.arpc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.archer.net.ChannelContext;
import com.archer.net.ssl.ProtocolVersion;
import com.archer.net.ssl.SslContext;
import com.archer.tools.arpc.ARPCClient;
import com.archer.tools.arpc.ARPCClientCallback;
import com.archer.tools.arpc.ARPCClientMessageListenner;
import com.archer.tools.arpc.ARPCMessageListenner;
import com.archer.tools.arpc.ARPCServer;
import com.archer.xjson.XJSONStatic;

public class ARPCTest {
	public static void test() {
		ARPCServer server = new ARPCServer("127.0.0.1", 9612);
		ARPCClient client = new ARPCClient("127.0.0.1", 9612);
		
		server.registerListener(new ARPCMessageListenner<MessageC, MessageB>(MessageC.class, MessageB.class) {
			@Override
			public MessageC onReceiveAndGenerateSend(ChannelContext ctx, MessageB recv) {
				System.out.println("server receive MessageB " + XJSONStatic.stringify(recv));
				return new MessageC();
			}
		});
		server.registerListener(new ARPCMessageListenner<MessageB, MessageA>(MessageB.class, MessageA.class) {
			@Override
			public MessageB onReceiveAndGenerateSend(ChannelContext ctx, MessageA recv) {
				System.out.println("server receive MessageA " + XJSONStatic.stringify(recv));
				return new MessageB();
			}
		});
		
		server.start();
		
		client.registerListener(new ARPCClientMessageListenner<MessageB, MessageC>(MessageB.class, MessageC.class));
		client.registerListener(new ARPCClientMessageListenner<MessageA, MessageB>(MessageA.class, MessageB.class));
		
		client.callRemoteAsync(new MessageB(), new ARPCClientCallback<MessageC>() {
			@Override
			public void onReceive(MessageC r) {
				System.out.println("client receive MessageC " + XJSONStatic.stringify(r));
			}
		});
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

		server.registerListener(new ARPCMessageListenner<MessageA, MessageB>(MessageA.class, MessageB.class) {
			@Override
			public MessageA onReceiveAndGenerateSend(ChannelContext ctx, MessageB recv) {
				System.out.println("server receive MessageB " + XJSONStatic.stringify(recv));
				return new MessageA();
			}
		});
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
		
		ARPCClient client = new ARPCClient("127.0.0.1", 9607, sslctx);
		client.registerListener(new ARPCClientMessageListenner<MessageB, MessageA>(MessageB.class, MessageA.class));
		MessageA recv = client.callRemote(new MessageB(), MessageA.class);
		System.out.println("recv a = " + XJSONStatic.stringify(recv));
	}
	
	public static void main(String args[]) {
//		test();
//		serverGmsslTest();
		clientGmsslTest();
	}
}
