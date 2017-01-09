package com.cmcc.demo;

import java.util.concurrent.locks.ReentrantLock;

/*
 * 线程在等待同步锁的时候，ReentrantLock可被中断，synchronized不可被中断
 * 意思是没有拿到锁的线程内部可不可以发生中断
 * 
 * reader线程在读取数据的时候等待时间太长，可被自身或者外部线程中断，中断后reader线程会抛出异常并被catch捕获，同时继续执行try-catch后面代码
 */
public class _2_ReentrantLockAndSynchronized {
    private static boolean useSyschronized = false;
    public static void main(String[] args) throws InterruptedException {
        IBuffer buffer = null;
        if(useSyschronized) {
            buffer = new Buffer();
        } else {
            buffer = new BufferInterruptibly();
        }
        
        Thread writer = new Thread(new Writer(buffer));
        Thread reader = new Thread(new Reader(buffer));
        writer.start();
        reader.start();
        
        new Thread(
                new Runnable() {
                    long startTime = System.currentTimeMillis();
                    
                    @Override
                    public void run() {
                        for(;;) {
                            if(System.currentTimeMillis() - startTime > 5000) {
                                System.out.println("不等了，尝试中断");
                                reader.interrupt();
                                break;
                            }
                        }
                    }
                }
        ).start();
    }
}

interface IBuffer {
    public void write();
    public void read() throws InterruptedException;
}

class Buffer implements IBuffer {

    private Object lock;
    
    public Buffer() {
        this.lock = this;
    }
    
    @Override
    public void write() {
        synchronized (this.lock) {
            long startTime = System.currentTimeMillis();
            System.out.println("开始写数据......");
            for(;;) {
                if(System.currentTimeMillis() - startTime > Integer.MAX_VALUE) {
                    break;
                }
            }
        }
    }

    @Override
    public void read() {
        synchronized (this.lock) {
            System.out.println("从buffer读取数据......");
        }
    }
}

class BufferInterruptibly implements IBuffer {

    ReentrantLock lock = new ReentrantLock();
    
    @Override
    public void write() {
        lock.lock();
        try {
            long startTime = System.currentTimeMillis();
            System.out.println("开始写数据......");
            for(;;) {
                if(System.currentTimeMillis() - startTime > Integer.MAX_VALUE) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void read() throws InterruptedException {
        lock.lockInterruptibly();
        
        try {
            System.out.println("从buffer读取数据......");
        } finally {
            lock.unlock();
        }
    }
}

class Writer implements Runnable {

    private IBuffer buffer;
    
    public Writer(IBuffer buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void run() {
        buffer.write();
    }
}

class Reader implements Runnable {
    private IBuffer buffer;
    
    public Reader(IBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            buffer.read();      //该线程在读取数据的时候等待时间太长，可被自身或者外部线程中断，中断后reader线程会抛出异常并被catch捕获，同时继续执行try-catch后面代码
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("我不读了");
        }
        
        System.out.println("读取结束");
    }
    
}