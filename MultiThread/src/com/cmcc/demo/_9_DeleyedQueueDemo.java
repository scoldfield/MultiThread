package com.cmcc.demo;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * DeleyedQueue使用：按照存储到队列中的顺序来取出数据，而且必须是delay到期后才能取。即使排在后面的已经倒计时结束了，排在前面的还没有倒计时结束，那么后面的也取不出来
 */
public class _9_DeleyedQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<Netizen> netizens = new DelayQueue<Netizen>();
        netizens.add(new Netizen(200));
        netizens.add(new Netizen(100));
        netizens.add(new Netizen(300));
        System.out.println("开始前，网民排序" + netizens);
        
        Monitor monitor = new Monitor(netizens);
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(monitor);
        
        TimeUnit.SECONDS.sleep(1000);
        exec.shutdownNow();
    }
}

/*
 * 网民对象，交钱上网
 */
class Netizen implements Delayed {

    private static int count = 0;
    private final int id = ++count;
    private int money;
    private long endTime;
    
    public Netizen(int money) {
        this.money = money;
        int duration = moneyToDuration(money);
        long currentTimeMillis = System.currentTimeMillis();    //当前时间：毫秒
        this.endTime = currentTimeMillis + duration;
    }
    
    /*
     * 将money转换成时间，计算出能上网到几点
     * 1元/20毫秒
     */
    private int moneyToDuration(int money) {
        int duration = money * 20;  //上网时长：毫秒
//        long currentTimeMillis = System.currentTimeMillis();    //当前时间：毫秒
//        long endTime = currentTimeMillis + duration;
        return duration;
    }
    
    public long getEndTime() {
        return endTime;
    }

    @Override
    public int compareTo(Delayed o) {
        if(o instanceof Netizen) {
            Netizen netizen = (Netizen)o;
            return (this.endTime - netizen.getEndTime() > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long currentTimeMillis = System.currentTimeMillis();    //当前时间：毫秒
        long delayed = this.endTime - currentTimeMillis;
        return delayed;
    }

    @Override
    public String toString() {
        return "Netizen [id=" + id + ", money=" + money + ", endTime=" + endTime + "]";
    }
}

/*
 * 网管监控网民上网时间
 */
class Monitor implements Runnable {

    DelayQueue<Netizen> netizens = new DelayQueue<Netizen>();
    
    public Monitor(DelayQueue<Netizen> netizens) {
        this.netizens = netizens;
    }
    
    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                System.out.println("网管检查...");
                Netizen netizen = netizens.take();  //如果队列中没有已经到期（endTime到期）的那就等待
                System.out.println(netizen + "时间到了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
