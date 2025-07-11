package com.archer.tools.threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ThreadTaskPool {
	
	private volatile boolean running;
	private Thread[] threads;
	private String namePrefix;
	private Consumer<Exception> exceptionHandler;
	
	private Object cond = new Object();
	private ConcurrentLinkedQueue<ThreadTask> queue = new ConcurrentLinkedQueue<>();
	
	public ThreadTaskPool() {
		this(2, null);
	}
	
	public ThreadTaskPool(int threadNum) {
		this(threadNum, ThreadTaskPool.class.getSimpleName());
	}
	
	public ThreadTaskPool(int threadNum, String namePrefix) {
		this.threads = new Thread[threadNum];
		this.running = false;
		this.namePrefix = namePrefix;
	}

	public ThreadTaskPool exceptionHandler(Consumer<Exception> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public void submit(ThreadTask task) {
		queue.offer(task);
		synchronized(cond) {
			cond.notify();
		}
	}
	
	
	public void start() {
		if(running) {
			return ;
		}
		running = true;
		for(int i = 0; i < threads.length; i++) {
			threads[i] = new PooledThread(this, i);
			threads[i].start();
		}
	}
	
	public void stop() {
		this.running = false;
		synchronized(cond) {
			cond.notify();
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private static class PooledThread extends Thread {
		
		ThreadTaskPool pool;
		
	    public PooledThread(ThreadTaskPool pool, int idx) {
	    	super(pool.namePrefix + "-" + idx);
			this.pool = pool;
		}

		@Override
	    public void run() {
			while(pool.running) {
				ThreadTask task = pool.queue.poll();
				if(task == null) {
					try {
						synchronized(pool.cond) {
							pool.cond.wait();
						}
					} catch (InterruptedException ignore) {}
					
					continue ;
				}
				try {
					task.run();
				} catch(Exception e) {
					if(pool.exceptionHandler != null) {
						pool.exceptionHandler.accept(e);
					} else {
						e.printStackTrace();
					}
				}
			}
	    }
	}
}
