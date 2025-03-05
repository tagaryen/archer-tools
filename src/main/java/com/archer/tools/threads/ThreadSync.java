package com.archer.tools.threads;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSync {

	private AtomicInteger threadCount;
	private Object lock = new Object();

	public ThreadSync(int threadTotal) {
		if(threadTotal < 1) {
			throw new IllegalArgumentException("threadTotal must be bigger than 0");
		}
		this.threadCount = new AtomicInteger(threadTotal);
	}
	
	public void countThread() {
		threadCount.decrementAndGet();
		synchronized(lock) {
			lock.notify();
		}
	}
	
	public void awaitThreads(long timeout) throws TimeoutException {
		long end = System.currentTimeMillis() + timeout, remain = timeout;
		while(!threadCount.compareAndSet(0, 0)) {
			synchronized(lock) {
				try {
					lock.wait(remain);
				} catch (InterruptedException ignore) {}
			}
			if(threadCount.compareAndSet(0, 0)) {
				return ;
			}
			if(System.currentTimeMillis() >= end) {
				throw new TimeoutException("timeout " + timeout);
			}
			remain = end - System.currentTimeMillis();
		}
	}
}
