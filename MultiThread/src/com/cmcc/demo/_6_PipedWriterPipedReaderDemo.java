package com.cmcc.demo;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * 线程间的协作：通过管道实现线程（任务）间的信息交换。PipedWriter/PipedReader
 * 一对一
 */
public class _6_PipedWriterPipedReaderDemo {

    public static void main(String[] args)
            throws IOException, InterruptedException {
        Sender sender = new Sender();
        Receiver receiver1 = new Receiver(sender);

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(sender);
        exec.execute(receiver1);

        TimeUnit.MILLISECONDS.sleep(5000);
        exec.shutdownNow();
    }
}

class Sender implements Runnable {

    private PipedWriter writer;

    public Sender() {
        this.writer = new PipedWriter(); // 默认构造函数即可将数据写到管道中去
    }

    public PipedWriter getWriter() {
        return writer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (!Thread.interrupted()) {     //不能写在外面，否则会被里面的while(true)屏蔽掉，导致while(!Thread.interrupted())只执行一次，以后都执行不到了
                    for (char c = 'a'; c <= 'z'; c++) {
                        writer.write(c);
                        TimeUnit.MILLISECONDS.sleep(20);
                    }
                }
            }
        } catch (IOException e) { // 用于捕获write()时的IO异常
            System.out.println("write IO Exception!");
//            e.printStackTrace();      //真实上线环境中不用打印异常堆栈信息
        } catch (InterruptedException e) { // 用于捕获sleep()阻塞的时候的被中断异常
            System.out.println("write sleep Exception!");
//            e.printStackTrace();
        }
    }
}

class Receiver implements Runnable {

    private PipedReader reader;

    public Receiver(Sender sender) throws IOException {
        this.reader = new PipedReader(sender.getWriter()); // 通过PipedReader的含参构造函数来将PipedWriter和PipedReader链接到同一个管道上
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (!Thread.interrupted()) {
                    char c = (char) reader.read();
                    System.out.println(c);
                }
            }
        } catch (IOException e) {
            System.out.println("read IO Exception!");
//            e.printStackTrace();
        }
    }
}