package Test14;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by weichen on 17/3/31.
 */
/*
ReentrantLock lock 使用方式机或者模板

try {
    lock.lock();
    ...
} finally {
    lock.unlock();
}
*/
public class TestReentrantLock {

    private ReentrantLock lock = new ReentrantLock();

    public void method1 () {
        try {
            lock.lock();
            System.out.println("当前线程:" + Thread.currentThread().getName() + "进入method1..");
            Thread.sleep(1000);
            System.out.println("当前线程:" + Thread.currentThread().getName() + "退出method1..");
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
            System.out.println("当前线程:" + Thread.currentThread().getName() + "进入method2..");
            Thread.sleep(2000);
            System.out.println("当前线程:" + Thread.currentThread().getName() + "退出method2..");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            lock.unlock();
        }
    }


    public static void main(String[] args) {

        final TestReentrantLock ur = new TestReentrantLock();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                ur.method1();
                ur.method2();
            }
        }, "t1");

        t1.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println(ur.lock.getQueueLength());
    }
}
