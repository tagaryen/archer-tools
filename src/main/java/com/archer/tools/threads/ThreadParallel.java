package com.archer.tools.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


public class ThreadParallel<P, R> {
	
	private volatile boolean running;
	private Thread[] threads;
	private String namePrefix;
	private AtomicInteger taskCount;
	private List<R> resultList;
	private Exception e;

	private Object resultCond = new Object();
	private Object cond = new Object();
	private ConcurrentLinkedQueue<PooledTask<P, R>> queue = new ConcurrentLinkedQueue<>();
	
	public ThreadParallel() {
		this(2, null);
	}
	
	public ThreadParallel(int threadNum) {
		this(threadNum, ThreadParallel.class.getSimpleName());
	}
	
	public ThreadParallel(int threadNum, String namePrefix) {
		this.threads = new Thread[threadNum];
		this.running = true;
		this.namePrefix = namePrefix;
		this.taskCount = new AtomicInteger(0);
		this.resultList = Collections.synchronizedList(new ArrayList<>(threadNum));
		this.e = null;

		for(int i = 0; i < threads.length; i++) {
			threads[i] = new PooledThread<P, R>(this, i);
			threads[i].start();
		}
	}
	
	public void submit(Function<P, R> consumer, P param) {
		queue.offer(new PooledTask<P, R>(param, consumer));
		taskCount.incrementAndGet();
		synchronized(cond) {
			cond.notify();
		}
	}
	
	public List<R> waitResultList(long timeout) throws Exception {
		long end = System.currentTimeMillis() + timeout, remain = timeout;
		while(!taskCount.compareAndSet(resultList.size(), resultList.size())) {
			synchronized(resultCond) {
				try {
					resultCond.wait(remain);
				} catch (InterruptedException ignore) {}
			}
			if(e != null) {
				throw e;
			}
			if(taskCount.compareAndSet(resultList.size(), resultList.size())) {
				stop();
				return resultList;
			}
			if(System.currentTimeMillis() >= end) {
				throw new TimeoutException("timeout " + timeout);
			}
			remain = end - System.currentTimeMillis();
		}
		stop();
		return resultList;
	}
	
	public void stop() {
		if(!this.running) {
			return ;
		}
		synchronized(cond) {
			cond.notifyAll();
		}
		this.running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private static class PooledTask<P, R> {
		P param;
		Function<P, R> consumer;
		
		public PooledTask(P param, Function<P, R> consumer) {
			this.param = param;
			this.consumer = consumer;
		}
	}
	
	private static class PooledThread<P, R> extends Thread {
		
		ThreadParallel<P, R> pool;
		
	    public PooledThread(ThreadParallel<P, R> pool, int idx) {
	    	super(pool.namePrefix + "-" + idx);
			this.pool = pool;
		}

		@Override
	    public void run() {
			while(pool.running) {
				PooledTask<P, R> task = pool.queue.poll();
				if(task == null) {
					try {
						synchronized(pool.cond) {
							pool.cond.wait();
						}
					} catch (InterruptedException ignore) {}
					
					continue ;
				}
				R ret;
				try {
					ret = task.consumer.apply(task.param);
				} catch(Exception e) {
					pool.e = e;
					ret = null;
				}
				pool.resultList.add(ret);
				synchronized(pool.resultCond) {
					pool.resultCond.notify();
				}
			}
	    }
	}
}

