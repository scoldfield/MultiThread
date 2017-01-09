package com.cmcc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * ReentrantLock可以被中断，而synchronized同步不可以被中断，这是ReentrantLock与synchronized的区别
 */
public class MultiThreadDemo3 {
    
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new Block());
        t.start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Issuing t.interrupt()");
        t.interrupt();
    }

    
}

class BlockedMutex {
    private Lock lock = new ReentrantLock();
    
    public BlockedMutex() {
        System.out.println(Thread.currentThread().toString() + " create BlockedMutex");
        lock.lock();
    }
    
    public void f() {
        try {
            lock.lockInterruptibly();
            System.out.println("lock acquired in f()");
        } catch (InterruptedException e) {
            System.out.println("Interrupted from lock acquisition in f()");
        }
    }
}

class Block implements Runnable{
    
    BlockedMutex blocked = new BlockedMutex();
    
    @Override
    public void run() {
        System.out.println("Waiting for f() in BlockedMutex");
        System.out.println(Thread.currentThread().toString() + " execute run()");
        blocked.f();
        System.out.println("Broken out of blocked call");
    }
}
