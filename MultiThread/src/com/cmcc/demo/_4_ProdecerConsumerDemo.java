package com.cmcc.demo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * 线程间的协作：使用wait()/notify()/notifyAll()
 * 使用wait()进行进程间的协作的经典案例：生产者-消费者问题
 * 
 * 前面讲解了如何解决线程间并发访问共享数据的问题，解决方法就是对操作共享数据的代码块加锁，形成序列任务。
 * 这一节讲解如何在线程间进行通信协作，使一个线程可以等待另一个线程完成后再继续执行
 * 
 * 总结：wait()/notify()/notifyAll()线程间协作方法需要与共享锁联合使用
 */
public class _4_ProdecerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> queue = new LinkedList<Integer>();
        int maxSize = 10;
        Thread t1 = new Thread(new Producer1(queue, maxSize));
        Thread t2 = new Thread(new Consumer1(queue));
        t1.start();
        t2.start();
        TimeUnit.MILLISECONDS.sleep(200);
        t1.interrupt();
        t2.interrupt();
    }
}

class Producer1 implements Runnable {

    private Queue<Integer> queue;
    private int MAX_SIZE;
    Random rand = new Random(47);
    
    public Producer1(Queue<Integer> queue, int maxSize) {
        this.queue = queue;
        this.MAX_SIZE = maxSize;
    }
    
    @Override
    public void run() {
        while(!Thread.interrupted()) {  //用于处理任务运行过程中的中断请求
            synchronized (queue) {      //生产者与消费者两个任务共享数据queue，因此需要对操作次共享数据的代码块进行同步。同时也是线程间协作的wait()方法的锁对象
                while(queue.size() == this.MAX_SIZE) {
                    try {
                        System.out.println("仓库满了，生产者不要再生产了");
                        queue.wait();
                    } catch (InterruptedException e) {      //用于处理任务调用wait()方法后处于阻塞状态时被中断的情况
                        System.out.println("Producter wait()方法出现异常");
                        e.printStackTrace();
                    }
                }
                
                System.out.println("生产者开始生产产品......");
                queue.add(rand.nextInt());
                System.out.println("生产者队列中的数据是：" + queue.toString());
                System.out.println("生产者通知消费者开始消费...");
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.notify();
            }
        }
    }
}

class Consumer1 implements Runnable {

    private Queue<Integer> queue;
    
    public Consumer1(Queue<Integer> queue) {
        this.queue = queue;
    }
    
    @Override
    public void run() {
        while(!Thread.interrupted()) {
            synchronized (queue) {
                while(queue.isEmpty()) {
                    try {
                        System.out.println("仓库已经没有事物了");
                        queue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Consumer wait()方法出现异常");
                        e.printStackTrace();
                    }
                }
                
                Integer head = queue.remove();
                System.out.println("消费者开始消费，取出的数据是" + head + "......");
                queue.notify();
            }
        }
    }
}
