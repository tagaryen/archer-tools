package com.archer.tools.threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ThreadVoidPool {
	
	private volatile boolean running;
	private Thread[] threads;
	private String namePrefix;
	private Consumer<Exception> exceptionHandler;
	
	private Object cond = new Object();
	private ConcurrentLinkedQueue<VoidTask> queue = new ConcurrentLinkedQueue<>();
	
	public ThreadVoidPool() {
		this(2, null);
	}
	
	public ThreadVoidPool(int threadNum) {
		this(threadNum, ThreadVoidPool.class.getSimpleName());
	}
	
	public ThreadVoidPool(int threadNum, String namePrefix) {
		this.threads = new Thread[threadNum];
		this.running = false;
		this.namePrefix = namePrefix;
	}

	public ThreadVoidPool exceptionHandler(Consumer<Exception> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public void submit(VoidTask task) {
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

	
	public interface VoidTask {
		void run();
	}
	
	private static class PooledThread extends Thread {
		
		ThreadVoidPool pool;
		
	    public PooledThread(ThreadVoidPool pool, int idx) {
	    	super(pool.namePrefix + "-" + idx);
			this.pool = pool;
		}

		@Override
	    public void run() {
			while(pool.running) {
				VoidTask task = pool.queue.poll();
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
