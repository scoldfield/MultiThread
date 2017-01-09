package com.cmcc.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
 * BlockingQueue：同步队列。
 * 上一节介绍的线程间的协作通信比较繁琐（需要通过调用同步锁的wait()/notify()实现，较复杂）。
 * 这一节介绍一个新的数据结构BlockingQueue同步队列，该队列在任何时刻只允许一个任务进行插入或者移除元素操作。因此，这个数据结构就实现了上一节的wait()/notify()的协作的目的。
 * BlockingQueue的实现类包括：LinkedBlockingQueue、ArrayBlockingQueue。前者是无界队列，后者是有界队列
 * 
 * 注意：BlockingQueue使用时要注意以下2点：
 * 1、实现类使用LinkedBlockingQueue或者ArrayBlockingQueue两种（Queue的实现类一般用LinkedList）
 * 2、存取数据用put()、take()。这两种方法会自动进行阻塞等待，实现了之前的手动wait()/notify()
 * 3、现在理解BlockingQueue的缺陷是：最好只用“一个对一个”，也就是说最好一个线程去队列中操作，另一个线程等待。不能有多个线程等待，否则次序会乱☆☆☆☆☆
 * 
 * 经典的吐司制作的示例代码：一台机器有3个任务：制作吐司，接着给吐司抹黄油，最后给吐司涂上果酱
 */
public class _5_BlockingQueueDemo {
    
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Toast> dryedQueue = new LinkedBlockingQueue<Toast>();
        BlockingQueue<Toast> butteredQueue = new LinkedBlockingQueue<Toast>();
        BlockingQueue<Toast> jummedQueue = new LinkedBlockingQueue<Toast>();
        Dryer dryer = new Dryer(dryedQueue);
        Butterer butterer = new Butterer(dryedQueue, butteredQueue);
        Jummer jummer = new Jummer(butteredQueue, jummedQueue);
        Eater eater = new Eater(jummedQueue);
        
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(dryer);
        exec.execute(butterer);
        exec.execute(jummer);
        exec.execute(eater);
        
        TimeUnit.MILLISECONDS.sleep(500);
        exec.shutdownNow();
    }
}

class Toast {
    public enum Status {DRY, BUTTERED, JUMMED};
    private Status status = Status.DRY; //当前Toast所处的状态
    private final int id;
    
    public Toast(int id) {
        this.id = id;
    }
    
    public void butter() {
        this.status = Status.BUTTERED;
    }
    
    public void jum() {
        this.status = Status.JUMMED;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Status getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "Toast " + id + ": " + this.status;
    }
}

class Dryer implements Runnable {

    private BlockingQueue<Toast> queue;
    private int count;
    
    public Dryer(BlockingQueue<Toast> queue) {
        this.queue = queue;
        this.count = 0;
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                Toast toast = new Toast(++count);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    this.queue.put(toast);  //这里需要用put()方法，已满时会自动进行阻塞等待
                } catch (InterruptedException e) {
                    System.out.println("线程阻塞的时候被中断");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Dryer try-catch interrupted");
        }
        
        System.out.println("Dryer off!");
    }
}

class Butterer implements Runnable {

    private BlockingQueue<Toast> dryedQueue;
    private BlockingQueue<Toast> butteredQueue;
    
    public Butterer(BlockingQueue<Toast> dryedQueue, BlockingQueue<Toast> butteredQueue) {
        this.dryedQueue = dryedQueue;
        this.butteredQueue = butteredQueue;
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                try {
                    Toast dryed = this.dryedQueue.take();
                    System.out.println(dryed);
                    dryed.butter();
                    this.butteredQueue.put(dryed);
                } catch (InterruptedException e) {
                    System.out.println("线程阻塞的时候被中断");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Butter try-catch interrupted");
        }
        
        System.out.println("Butter off!");
    }
}

class Jummer implements Runnable {

    private BlockingQueue<Toast> butteredQueue;
    private BlockingQueue<Toast> jummedQueue;
    
    public Jummer(BlockingQueue<Toast> butteredQueue, BlockingQueue<Toast> jummedQueue) {
        this.butteredQueue = butteredQueue;
        this.jummedQueue = jummedQueue;
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                try {
                    Toast buttered = butteredQueue.take();
                    System.out.println(buttered);
                    buttered.jum();
                    jummedQueue.put(buttered);
                } catch (InterruptedException e) {
                    System.out.println("线程阻塞的时候被中断");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Jummer try-catch interrupted");
        }
        
        System.out.println("Jummer off!");
    }
}

class Eater implements Runnable {
    private int count = 0;
    private BlockingQueue<Toast> jummedQueue;
    
    public Eater(BlockingQueue<Toast> jummedQueue) {
        this.jummedQueue = jummedQueue;
    }
    
    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                try {
                    Toast jummed = jummedQueue.take();
                    System.out.println(jummed);
                    
                    //吐司校验
                    if(jummed.getId() != ++count || jummed.getStatus() != Toast.Status.JUMMED) {
                        System.out.println("Error" + jummed);
                        System.exit(0);
                    } else {
                        System.out.println("champ " + jummed);
                    }
                } catch (InterruptedException e) {
                    System.out.println("线程阻塞的时候被中断");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Eater try-catch interrupted");
        }
        
        System.out.println("Eater off!");
    }
}