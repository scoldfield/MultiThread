package com.cmcc.demo;

//多线程的实例。开启子线程
public class MultiThreadDemo1 {

    public static void main(String[] args) {

        System.out.println("++++++++主线程开启++++++++");

        /*
         * 方法一：
         * 
         * 0、通过Thread类的构造函数Thread()方法实现
         * 1、自定义类继承Thread类
         * 2、实现Thread类中的run()方法。所有子线程要进行的操作都在run()方法中实现
         * 3、开启线程。start()方法。start()方法即执行run()方法中的所有操作
         * 
         * 对Java来说，run()方法没有任何特别之处。像main()方法一样，它只是新线程知道
         * 调用的方法名称(和签名)。因此，在Runnable上或者Thread上调用run方法是合法的。 但并不启动新的线程。
         * 
         * Thread thread1 = new Thread() { public void run() {
         * System.out.println("---------子线程开启1---------");
         * 
         * for(int i = 0; i < 5; i++) { System.out.println("subThread1:" + i); }
         * } };
         * 
         * Thread thread2 = new Thread() { public void run() {
         * System.out.println("---------子线程开启2---------");
         * 
         * for(int i = 0; i < 5; i++) { System.out.println("subThread2:" + i); }
         * } };
         * 
         */

        /*
         * 方法二
         * 
         * 0、通过Thread类的构造函数Thread(Runnable target)方法实现
         * 1、自定义类继承Thread(Runnable target)类
         * 2、实现Runnable接口中的run()方法。所有子线程要进行的操作都在run()方法中实现
         * 3、开启线程。start()方法。start()方法即执行run()方法中的所有操作
         */

        Thread thread1 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("---------子线程开启1---------");

                for (int i = 0; i < 5; i++) {
                    System.out.println("subThread1:" + i);
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("---------子线程开启2---------");

                for (int i = 0; i < 5; i++) {
                    System.out.println("subThread2:" + i);
                }
            }
        });
        
        thread1.start(); 
        thread2.start();
    }
}
