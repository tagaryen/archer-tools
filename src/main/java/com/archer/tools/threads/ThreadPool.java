package com.archer.tools.threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;


public class ThreadPool<T> {
	
	private volatile boolean running;
	private Thread[] threads;
	private String namePrefix;
	private Consumer<Exception> exceptionHandler;
	
	private Object cond = new Object();
	private ConcurrentLinkedQueue<PooledTask<T>> queue = new ConcurrentLinkedQueue<>();
	
	public ThreadPool() {
		this(2, null);
	}
	
	public ThreadPool(int threadNum) {
		this(threadNum, ThreadPool.class.getSimpleName());
	}
	
	public ThreadPool(int threadNum, String namePrefix) {
		this.threads = new Thread[threadNum];
		this.running = false;
		this.namePrefix = namePrefix;
	}
	
	public ThreadPool<T> exceptionHandler(Consumer<Exception> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}
	
	public void submit(Consumer<T> consumer, T param) {
		queue.offer(new PooledTask<T>(param, consumer));
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
			threads[i] = new PooledThread<T>(this, i);
			threads[i].start();
		}
	}
	
	public void stop() {
		synchronized(cond) {
			cond.notifyAll();
		}
		this.running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private static class PooledTask<T> {
		T param;
		Consumer<T> consumer;
		
		public PooledTask(T param, Consumer<T> consumer) {
			this.param = param;
			this.consumer = consumer;
		}
	}
	
	private static class PooledThread<T> extends Thread {
		
		ThreadPool<T> pool;
		
	    public PooledThread(ThreadPool<T> pool, int idx) {
	    	super(pool.namePrefix + "-" + idx);
			this.pool = pool;
		}

		@Override
	    public void run() {
			while(pool.running) {
				PooledTask<T> task = pool.queue.poll();
				if(task == null) {
					try {
						synchronized(pool.cond) {
							pool.cond.wait();
						}
					} catch (InterruptedException ignore) {}
					
					continue ;
				}
				try {
					task.consumer.accept(task.param);
				} catch(Exception e) {
					if(pool.exceptionHandler != null) {
						pool.exceptionHandler.accept(e);
					}
				}
			}
	    }
	}
}

