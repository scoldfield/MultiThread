package com.cmcc.demo;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class _11_ScheduledExecutor {

	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService sexec = Executors.newScheduledThreadPool(2);
		/*
		 * 用法1：ScheduledExecutorService.schedule()方法的使用：
		 * 任务在线程启动后延迟delay后再启动，且任务只执行一次
		TimeUnit unit = TimeUnit.MILLISECONDS;
		long delay = 4;
		sexec.schedule(new Worker(), delay, unit);	//延迟delay时间后，只执行任务一次
		 */
		/*
		 * 用法2：ScheduledExecutorService.scheduleWithFixedDelay()方法的使用：
		 * 任务比线程延迟initialDelay执行，且任务可循环执行，循环的间隔是delay
		 * 下一次任务的执行一定是在上一次任务执行完之后，再延迟delay时间后才启动执行，不存在并行任务执行
		long initialDelay = 2;	//线程启动后，任务延迟5s后再开始执行
		long delay = 2;		//任务间间隔执行的时间是2s
		TimeUnit unit = TimeUnit.SECONDS;
		sexec.scheduleWithFixedDelay(new Worker(), initialDelay, delay, unit);
		 */
		
		/*
		 * 用法3：ScheduledExecutorService.scheduleAtFixedRate()方法的使用
		 * 定时启动任务。与用法2的区别是：用法2是循环执行任务，一个执行完之后另一个继续执行；用法3是定时执行任务，即设置3点执行，那任务就会3点执行，并且循环执行下去
		 * 若到达预定时间，但是上一次任务没有执行完，那么下一个任务将阻塞，知道上一个任务执行完后立即启动，而不会与上一个任务产生并行执行的问题。
		 */
		long initialDelay = 2;	//线程启动后，任务延迟5s后再开始执行
		long period = 1;	//定期执行任务，即设定任务在固定的时间执行
		TimeUnit unit = TimeUnit.SECONDS;
		sexec.scheduleAtFixedRate(new Worker(), initialDelay, period, unit);
		TimeUnit.SECONDS.sleep(200);
		sexec.shutdownNow();
	}
}

class Worker implements Runnable {

	public void run() {
		try {
			System.out.println("++++++++线程" + Thread.currentThread().toString() + "开始执行时间：" + new Date());
			TimeUnit.SECONDS.sleep(5);	//任务执行耗费1s
			System.out.println("---------------线程" + Thread.currentThread().toString() + "执行结束时间：" + new Date());
		} catch (InterruptedException e) {
			System.out.println("worker sleep interrupted!");
			e.printStackTrace();
		}
	}
}