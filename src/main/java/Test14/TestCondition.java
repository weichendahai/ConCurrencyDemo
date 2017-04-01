package Test14;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by weichen on 17/3/31.
 */
/*
ReentrantLock lock 使用方式或者模板

try {
    lock.lock();
    ...
} finally {
    lock.unlock();
}
*/
public class TestCondition {

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void method1 () {
        try {
            lock.lock();
            System.out.println("当前线程:" + Thread.currentThread().getName() + "进入method1 ..");
            Thread.sleep(1000);
            System.out.println("当前线程：" + Thread.currentThread().getName() + "释放锁 ..");
            condition.await();
            System.out.println("当前线程:" + Thread.currentThread().getName() + "退出method1 ..");
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void method2(){
        try {
            lock.lock();
            System.out.println("当前线程:" + Thread.currentThread().getName() + "进入method2 ..");
            Thread.sleep(2000);
            System.out.println("当前线程：" + Thread.currentThread().getName() + "发出唤醒 ..");
            condition.signal();
            System.out.println("当前线程:" + Thread.currentThread().getName() + "退出method2 ..");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {

        final TestCondition testCondition = new TestCondition();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                testCondition.method1();
            }
        }, "t1");
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                testCondition.method2();
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
