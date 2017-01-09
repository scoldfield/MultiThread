package com.cmcc.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * CountDownLatch：是condition.await()/asignal()方法的升级
 */
public class _7_CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Worker1("张三", latch));
        exec.execute(new Worker1("李四", latch));
        latch.await();
        System.out.println("两个工人事情都做完了，主线程被唤醒");
        exec.shutdownNow();
    }
}

class Worker1 implements Runnable {

    private String name;
    private CountDownLatch latch;
    
    public Worker1(String name, CountDownLatch latch) {
        this.name = name;
        this.latch = latch;
    }
    
    @Override
    public void run() {
        for(int i = 0; i < 100; i++) {
            try {
                System.out.println(this.name + ": i = " + i);
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("worker sleep interrupted!");
                e.printStackTrace();
            }
        }

        System.out.println(this.name + ": 开始执行CountDownLatch.countDown()");
        latch.countDown();  //工人事情做完后倒计时，唤醒等待的主线程。
        System.out.println("latch.getCount() = " + latch.getCount());
    }
}