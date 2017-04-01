package Test07;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by weichen on 17/3/29.
 */

/*

二. 线程间通信

使用 wait/notify 方法实现线程间通信
1.wait/notify方法 必须配合 synchronized 关键字一起使用
2.wait 方法释放锁，然后堵塞线程；notify 方法通信另外线程，但是并不释放锁
wait 释放锁，notify不释放锁

 注意：
 （wait/notify 两个方法都是Object类的方法，就是说，java语言为所有对象提供了这两个方法）


*/
public class TestThreadCountDown {

    private volatile static List list = new ArrayList();

    public void addItem () {
        list.add("welcom to beijing");
    }

    public int size () {
        return list.size();
    }

    //====================

    public static void main(String[] args) {

        String ExampleInfo = String.format("%s\n %s\n",
                "   Example info    ",
                "== 测试线程通信 33 ==");
        System.out.println(ExampleInfo);

         // Error:(53, 25) java: 从内部类中访问本地变量testThreadWaitNotify1; 需要被声明为最终类型
         //TestThreadWaitNotify1 testThreadWaitNotify1 = new TestThreadWaitNotify1();

        final TestThreadCountDown testThreadCountDown = new TestThreadCountDown();
//        final Object lock = new Object();

        //package java.util.concurrent; 包下提供的并发编程包；
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
//                    synchronized (lock) {
                        System.out.println("线程t1 启动");
                        for (int i = 0; i<10; i++) {
                            testThreadCountDown.addItem();
                            System.out.println("当前线程:" + Thread.currentThread().getName() + " 添加了一个元素");
                            Thread.sleep(500);

                            if (testThreadCountDown.size() == 5 ) {
                                //notify 不释放锁；继续执行当前线程；直至线程执行结束释放锁
                                //此刻，并没有真实发出通知，直至线程结束，释放锁，发送通知消息
                                //所以你看到的log，发出信息和收到信息不是同时的，有个时间差
//                                lock.notify();
                                countDownLatch.countDown();
                                System.out.println("线程:" + Thread.currentThread().getName() + "已经发出通知 .. "+" size="+testThreadCountDown.size());
                            }
//                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {

//                synchronized (lock) {
                    System.out.println("线程t2 启动");
                    if (testThreadCountDown.size() != 5) {
                        try {
                            //wait 释放锁；然后堵塞
//                            lock.wait();
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("当前线程收到了通知：" + Thread.currentThread().getName() + " liset size=" + testThreadCountDown.size() + "线程停止");
                    throw new RuntimeException();
//                }
            }
        },"t2");

        t2.start();
        t1.start();
    }

}

/*
//运行log

     Example info
 == 测试线程通信 33 ==

线程t2 启动
线程t1 启动
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
线程:t1已经发出通知 ..  size=5
Exception in thread "t2" java.lang.RuntimeException
当前线程:t1 添加了一个元素
	at Test07.TestThreadCountDown$2.run(TestThreadCountDown.java:97)
当前线程收到了通知：t2 liset size=5线程停止
	at java.lang.Thread.run(Thread.java:745)
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素

Process finished with exit code 0
*/