package Test07;

import java.util.ArrayList;
import java.util.List;

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


/*
示例说明:
t2 线程启动synchronized 获取锁，然后wait堵塞，释放锁
t1 线程添加10个元素到list 数组中，当添加到list.size=5时候向t2线程发送通知消息；但是并不释放锁，直至t1线程完成
t2 线程收到t1线程在list.size=5时通知，无法执行（t1不释放锁）；抢到锁时，运行t2线程，此时list.size=10

注释:synchronized 陪着wait/notify 有一定延迟不能实时完成
*/

public class TestThreadWaitNotify2 {

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
                "== 测试线程通信 22 ==");
        System.out.println(ExampleInfo);

         // Error:(53, 25) java: 从内部类中访问本地变量testThreadWaitNotify1; 需要被声明为最终类型
         //TestThreadWaitNotify1 testThreadWaitNotify1 = new TestThreadWaitNotify1();

        final TestThreadWaitNotify2 testThreadWaitNotify2 = new TestThreadWaitNotify2();
        final Object lock = new Object();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized (lock) {
                        System.out.println("线程t1 启动");
                        for (int i = 0; i<10; i++) {
                            testThreadWaitNotify2.addItem();
                            System.out.println("当前线程:" + Thread.currentThread().getName() + " 添加了一个元素");
                            Thread.sleep(500);

                            if (testThreadWaitNotify2.size() == 5 ) {
                                //notify 不释放锁；继续执行当前线程；直至线程执行结束释放锁
                                //此刻，并没有真实发出通知，直至线程结束，释放锁，发送通知消息
                                //所以你看到的log，发出信息和收到信息不是同时的，有个时间差
                                lock.notify();
                                System.out.println("线程:" + Thread.currentThread().getName() + "已经发出通知 .. "+" size="+testThreadWaitNotify2.size());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {

                synchronized (lock) {
                    System.out.println("线程t2 启动");
                    if (testThreadWaitNotify2.size() != 5) {
                        try {
                            //wait 释放锁；然后堵塞
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("当前线程收到了通知：" + Thread.currentThread().getName() + " liset size=" + testThreadWaitNotify2.size() + "线程停止");
                    throw new RuntimeException();
                }
            }
        },"t2");

        t2.start();
        t1.start();
    }

}

/*
//运行log

  Example info
 == 测试线程通信 22 ==

线程t2 启动
线程t1 启动
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
线程:t1已经发出通知 ..  size=5
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程收到了通知：t2 liset size=10线程停止
Exception in thread "t2" java.lang.RuntimeException
	at Test07.TestThreadWaitNotify2$2.run(TestThreadWaitNotify2.java:90)
	at java.lang.Thread.run(Thread.java:745)

Process finished with exit code 0
*/