package com.archer.tools.test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.archer.tools.threads.ThreadParallel;
import com.archer.tools.threads.ThreadSync;

public class ThreadsTest {
	
	static class ParallelC {
		String name;

		public ParallelC(String name) {
			super();
			this.name = name;
		}
		
	}
	
	public static void syncTest() {
		ThreadSync sync = new ThreadSync(4);
		Thread[] threads = new Thread[4];
		for(int i = 0; i < 4; i++) {
			threads[i] = new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("after sleep, id = " + Thread.currentThread().getId());
					sync.countThread();
				}
			};
			threads[i].start();
		}
		System.out.println("print test1");
		try {
			sync.awaitThreads(1500);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println("print test2");
	}
	
	public static void multiTest() {
		ThreadParallel<ParallelC, ParallelC> multi = new ThreadParallel<>(3);
		Random r = new Random();
		for(int i = 0; i < 5; i++) {
			multi.submit((p) -> {
				try {
					Thread.sleep(r.nextInt(200) + 200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				p.name += ", haoshuai";
				return p;
			}, new ParallelC("xuyi" + i));
		}
		System.out.println("print test1");
		try {
			List<ParallelC> ret = multi.waitResultList(1000);
			for(ParallelC c: ret) {
				System.out.println(c.name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("print test2");
	}
	
	
	public static void main(String args[]) {
		multiTest();
	}
}
