package Test15;

import java.util.concurrent.CountDownLatch;

/**
 * Created by weichen on 17/4/1.
 */

/*
CountDownLatch 倒计时；就是一个线程，等待其他线程通知；需要通知倒计时次数
次数达到，此线程继续执行；否则就等待;

CountDownLatch 接受完所有消息后就会立即运行；不会等到其他所有线程完成再运行
countDownLatch.await();
countDownLoatch.countDown();

使用场景:比如用于监听某个系统初始化时，各种检测配置环境的检测什么的；
*/

/*

代码说明：
1. 创建一个CountDownLatch，在t1线程等待,待接手2个通知消息后继续运行
2. 创建t2,t3线程，然后通知t1继续

*/
public class TestCountDownLatch {

    public static void main(String[] args) {

        //主线程；定义接受通知个数
        int notifyCount = 2;
        final CountDownLatch countDownLatch = new CountDownLatch(notifyCount);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("进入线程:" + Thread.currentThread().getName() + "等待其他所有线程完成" );
                try {
                    Thread.sleep(1000);
                    //堵塞；等待接受其他线程通知
                    countDownLatch.await();
                    System.out.println("t1 线程继续运行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("t2线程进行初始化操作...");
                    Thread.sleep(3000);
                    System.out.println("t2线程初始化完毕，通知t1线程继续...");
//                    t2运行完成；通知t1线程
                    //不要使用
                    //countDownLatch.notify();
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t2");

        Thread t3 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("t3线程进行初始化操作...");
                    Thread.sleep(3000);
                    System.out.println("t3线程初始化完毕，通知t1线程继续...");
                    //                    t2运行完成；通知t1线程
                    countDownLatch.countDown();
                    System.out.println("通知完；还在执行 ...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t3");

        t1.start();
        t2.start();
        t3.start();
    }
}

/*
log

进入线程:t1等待其他所有线程完成
t2线程进行初始化操作...
t3线程进行初始化操作...
t2线程初始化完毕，通知t1线程继续...
t3线程初始化完毕，通知t1线程继续...
通知完；还在执行 ...
t1 线程继续运行

*/