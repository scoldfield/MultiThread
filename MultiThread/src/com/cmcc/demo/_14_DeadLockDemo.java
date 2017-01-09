package com.cmcc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * 一个简单的java多线程死锁示例
 */
public class _14_DeadLockDemo {
	
	public static void main(String[] args) throws InterruptedException {
		Object lock1 = new Object();
		Object lock2 = new Object();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new ThreadDemo1(lock1, lock2));
		exec.execute(new ThreadDemo2(lock1, lock2));
		
		System.out.println("主线程等待10s后杀死死锁线程");
		TimeUnit.SECONDS.sleep(10);
		exec.shutdownNow();
		System.out.println("两个死锁线程已经被杀死，主线程结束");
	}
}

class ThreadDemo1 implements Runnable {

	private Object lock1;
	private Object lock2;
	
	public ThreadDemo1() {
		super();
	}

	public ThreadDemo1(Object lock1, Object lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName() + "线程启动");
			synchronized (lock1) {	//线程启动后拿到两把锁lock1，lock2后才能执行业务代码，此时已经拿到lock1了
				TimeUnit.MILLISECONDS.sleep(50);	//这里sleep一下是为了防止该线程启动后又拿到lock2锁，那么ThreadDemo2就不会拿到lock2，那么就不会发生死锁。因此这个休眠是为了便于ThreadDemo2拿到lock2锁，更利于死锁发生
				synchronized (lock2) {
					//do sth，业务代码
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ThreadDemo2 implements Runnable {
	
	private Object lock1;
	private Object lock2;
	
	public ThreadDemo2() {
		super();
	}
	
	public ThreadDemo2(Object lock1, Object lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName() + "线程启动");
			synchronized (lock2) {
				TimeUnit.MILLISECONDS.sleep(50);
				synchronized (lock1) {
					//do sth
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}