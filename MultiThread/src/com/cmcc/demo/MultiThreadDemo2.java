package com.cmcc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadDemo2 {
    
    public void test1() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i = 0; i < 4; i++) {
            exec.execute(new CountTask1(i));
        }
        
        TimeUnit.MILLISECONDS.sleep(2);
        CountTask1.cancel();
        exec.shutdownNow();
    }
    
    public void test2() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        CountTask2 countTask2 = new CountTask2();
        for(int i = 0; i < 3; i++) {
            exec.execute(countTask2);
        }
        
        TimeUnit.MILLISECONDS.sleep(2);
        CountTask2.cancel();
        exec.shutdownNow();
    }
    
    public static void main(String[] args) throws InterruptedException {
        new MultiThreadDemo2().test2();
    }

}


class CountTask1 implements Runnable{

    private int id;
    private int count = 0;
    private static boolean canceled = false;
    
    public CountTask1(int id) {
        this.id = id;
    }

    private synchronized void increment() {
        count++;
    }
    
    private int getCount() {
        return count;
    }
    
    public static void cancel() {
        canceled = true;
    }
    
    @Override
    public void run() {
        while(!canceled) {
            increment();
            System.out.println("CountTask-" + id + ", count = " + getCount());
        }
    }
}

class CountTask2 implements Runnable{

    private int count = 0;
    private static boolean canceled = false;
    
    public CountTask2() {
    }

    public synchronized void increment() {
        count++;
        System.out.println(toString());
    }
    
    public static void cancel() {
        canceled = true;
    }
    
    @Override
    public void run() {
        while(!canceled) {
            increment();
        }
    }
    
    @Override
    public String toString() {
        return Thread.currentThread().toString() + ":count = " + count;
    }
}
