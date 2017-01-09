package com.cmcc.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * CyclicBarrier：子线程间互相等待，不需要主线程操作，只要主线程等待足够长的时间即可
 */
public class _8_CyclicBarrierDemo {

    private static int N = 3;
    
    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(N);
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i = 0; i < N; i++) {
            exec.execute(new Runner("选手" + i, barrier));
        }
        
        TimeUnit.SECONDS.sleep(4);
        exec.shutdownNow();
    }
}

class Runner implements Runnable {
    private String name;
    private CyclicBarrier barrier;
    
    public Runner(String name, CyclicBarrier barrier) {
        this.name = name;
        this.barrier = barrier;
    }
    
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(this.name + "准备好了");
            barrier.await();
        } catch (InterruptedException e) {
            System.out.println(this.name + " sleep interrupted");
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            System.out.println(this.name + " await broken");
            e.printStackTrace();
        }
        
        System.out.println("起跑");
    }
}