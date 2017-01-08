package com.cmcc.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Exchanger：仅仅用于两个线程间交换信息
 * exchanger.exchange()方法调用后，该线程将进入阻塞，直到另外一个线程也调用了exhanger.exchange()方法
 */
public class _13_ExchangerDemo {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec = Executors.newCachedThreadPool();
		Exchanger<List<String>> exchanger = new Exchanger<List<String>>();
		exec.execute(new Producer(exchanger));
		exec.execute(new Consumer(exchanger));
		
		TimeUnit.SECONDS.sleep(8);
		System.out.println("数据交换结束");
		exec.shutdownNow();
	}
}

class Producer implements Runnable {

	List<String> list = new ArrayList<String>();
	private Exchanger<List<String>> exchanger;
	
	public Producer(Exchanger<List<String>> exchanger) {
		this.exchanger = exchanger;
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
	}
	
	@Override
	public void run() {
		try {
			System.out.println("producer 准备交换数据+++++++");
 			List<String> exchanged = exchanger.exchange(list);
 			System.out.println("producer 接收到的数据是：" + exchanged);
 			System.out.println("producer 交换数据结束-------");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Consumer implements Runnable {

	List<String> list = new ArrayList<String>();
	Exchanger<List<String>> exchanger;
	
	public Consumer(Exchanger<List<String>> exchanger) {
		this.exchanger = exchanger;
		list.add("6");
		list.add("7");
		list.add("8");
		list.add("9");
		list.add("10");
	}
		
	@Override
	public void run() {
		try {
			System.out.println("Consumer 开始休息5s");
			TimeUnit.SECONDS.sleep(5);
			
			System.out.println("Consumer 准备交换数据");
			List<String> exchanged = exchanger.exchange(list);
			System.out.println("Consumer 接收到的数据是：" + exchanged);
			System.out.println("Consumer 数据交换完成");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
