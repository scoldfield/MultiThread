package com.cmcc.demo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Semaphore的使用
 */
public class _12_SemaphoreDemo {

	private static Random rand = new Random(47);
	private static Integer acquire = null;
	
	public static void main(String[] args) throws InterruptedException {
		LinkedList<Integer> list = new LinkedList<Integer>();
		List<Integer> syncList = Collections.synchronizedList(list);
		
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < 5; i++) {
			exec.execute(new Runnable() {
				
				@Override
				public void run() {
					syncList.add(rand.nextInt(50));		//不能使用i来向syncList中添加元素，因为虽然syncList是同步的，但是i没有同步，也是线程间的共享元素
				}
			});
		}
		TimeUnit.MILLISECONDS.sleep(2000);
		System.out.println("启动5个线程向list中添加元素，list = " + list.toString());
		
		SemaphoreResource resource = new SemaphoreResource(list);
		for(int i = 0; i < 10; i++){
			
			exec.execute(new Runnable() {
				@Override
				public void run() {
					acquire = resource.acquire();
					System.out.println(Thread.currentThread().toString() + "线程获取到的元素是：" + acquire);
					
					if(acquire != null){
						resource.addLast(acquire);
					}
				}
			});
		}
		TimeUnit.MILLISECONDS.sleep(2000);
		exec.shutdownNow();
		System.out.println("任务结束");
	}
}

class SemaphoreResource {
	private Semaphore semaphore;
	private LinkedList<Integer> list = new LinkedList<Integer>();
	private Lock lock = new ReentrantLock();
	
	public SemaphoreResource(LinkedList<Integer> list) {
		this.list = list;
		this.semaphore = new Semaphore(list.size());
	}
	
	public Integer acquire(){
		lock.lock();
		try {
			Integer first = list.pollFirst();
			semaphore.acquire();
			return first;
		} catch (InterruptedException e) {
			System.out.println("semaphore.acquire() interrupted");
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		return null;
	}
	
	public void addLast(Integer i){
		lock.lock();
		try {
			list.addLast(i);
			semaphore.release();
		} catch (Exception e) {
			System.out.println("add interrupted");
		} finally {
			lock.unlock();
		}
		
	}
}