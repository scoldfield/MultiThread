package com.cmcc.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * 同步花园的例子：即花园有5个入口，每个入口都会有人进入，统计一天总共的进园人数
 */
public class _1_SynchronizedGardenDemo {
    
    public static void main(String[] args) throws InterruptedException {
        /*
         * 需要统计每个入口的游客数
         * */
        List<Entrance1> entrs = new ArrayList<Entrance1>();
        Entrance1 entr1 = new Entrance1();
        Entrance1 entr2 = new Entrance1();
        Entrance1 entr3 = new Entrance1();
        Entrance1 entr4 = new Entrance1();
        entrs.add(entr1);
        entrs.add(entr2);
        entrs.add(entr3);
        entrs.add(entr4);
        
        new Thread(entr1).start();
        new Thread(entr2).start();
        new Thread(entr3).start();
        new Thread(entr4).start();
        
        TimeUnit.MILLISECONDS.sleep(50);
        Entrance1.cancel();
        
        for(Entrance1 entr : entrs) {
            System.out.println(entr);
        }
         
        
        /*
         * 不需要统计每个入口的游客数
        Entrance2 entr2 = new Entrance2();
        System.out.println("before " + entr2);
        
        for(int i = 0; i < 4; i++) {
            new Thread(entr2).start();
        }
        
        TimeUnit.MILLISECONDS.sleep(50);
        entr2.cancel();

        System.out.println("after " + entr2);
        */
    }
    
}

/*
 * 不需要记录每个入口的游客数的话使用
 * 思路：启动4个线程来修改同一个对象中的数据。对于修改的方法需要同步
 */
class Entrance2 implements Runnable {

    private int all = 0;
    private boolean canceled = false;
    
    public synchronized void incAll() {
        this.all ++;
        System.out.println(this);
    }
    
    public void cancel() {
        this.canceled = true;
    }
    
    @Override
    public String toString() {
        return Thread.currentThread().getName() + ", all = " + all;
    }
    
    @Override
    public void run() {
        while(!canceled) {
            incAll();
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped ");
    }
    
}

/*
 * 需要记录每个入口的游客数时使用
 * 思路：生成4个对象，每个线程运行一个对象。
 * 该对象中有一个static属性，用来记录总共游客数，即all。这个属性需要用static修饰，否则由于生成4个对象，每个中的all属性都会被初始化为0，
 * 当然也可以不用这个属性，程序运行完后统计每个线程中的count属性之和也可以
 */
class Entrance1 implements Runnable {
    private static int all = 0;     //总共游客数
    private int count;  //每个入口各自的进园人数
    private static boolean canceled = false;
    
    public Entrance1() {
        this.count = 0;
    }
    
    //游客进入
    public void increase() {
        count ++;
        System.out.println(this);
    }
    
    public synchronized void sum() {
        all ++;
        System.out.println(this);
    }
    
    public static void cancel() {
        canceled = true;
    }
    
    public static int getAll() {
        return all;
    }
    
    @Override
    public String toString() {
        return Thread.currentThread().getName() + ", count = " + count + ", all = " + all;
    }
    
    @Override
    public void run() {
        while(!canceled) {
            increase();
            sum();
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(Thread.currentThread().getName() + " stopped");
    }
}