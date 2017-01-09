package com.cmcc.demo;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

/*
 * PriorityBlockingQueue：优先级队列。
 * 1、必须实现Comparable接口
 * 2、向其中添加元素后会与队列中对头部分元素进行排序
 * 3、始终保持对头元素优先级最高
 * 
 * 感觉就是一个单线程的队列
 */
public class _10_PriorityBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        PriorityBlockingQueue<PriorityEntity> queue = new PriorityBlockingQueue<PriorityEntity>();
        Random rand = new Random(47);
        for(int i = 0; i < 10; i++) {
            queue.add(new PriorityEntity(rand.nextInt(20), i));
        }
        
        while(true) {
            PriorityEntity entity = queue.take();
            System.out.println("entity = " + entity);
        }
    }
}

class PriorityEntity implements Comparable<PriorityEntity> {

    private int priority;
    private int index;
    
    public PriorityEntity(int priority, int index) {
        this.priority = priority;
        this.index = index;
    }
    
    @Override
    public int compareTo(PriorityEntity o) {
        
        return (this.priority > o.getPriority() ? 1 : (this.priority == o.getPriority() ? 0 : -1));
    }
    
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "PriorityEntity [priority=" + priority + ", index=" + index
                + "]";
    }
}