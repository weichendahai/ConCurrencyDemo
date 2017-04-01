package Test14;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.*;

/**
 * Created by weichen on 17/3/31.
 */

/*
读写锁
readwrite lock

读读共享；读写互斥；写写互斥
*/
public class TestReadWriteLock {

    //创建一个reentrantReadWriteLock
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private ReadLock readLock = rwLock.readLock();
    private WriteLock writeLock = rwLock.writeLock();

    public void read() {
        try {
            readLock.lock();
            System.out.println("占用 读取锁；当前线程:" + Thread.currentThread().getName() + "进入 ...");
            Thread.sleep(3000);
            System.out.println("释放 读取锁；当前线程:" + Thread.currentThread().getName() + "退出 ...");
        }catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    public void write () {
        try {
            writeLock.lock();
            System.out.println("占用 写入锁；当前线程:" + Thread.currentThread().getName() + "进入 ...");
            Thread.sleep(3000);
            System.out.println("释放 写入锁；当前线程:" + Thread.currentThread().getName() + "退出 ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        final TestReadWriteLock testReadWriteLock = new TestReadWriteLock();

        Thread tRead1 = new Thread(new Runnable() {
            public void run() {
                testReadWriteLock.read();
            }
        },"tRead1");

        Thread tRead2 = new Thread(new Runnable() {
            public void run() {
                testReadWriteLock.read();
            }
        },"tRead2");

        Thread tWrite1 = new Thread(new Runnable() {
            public void run() {
                testReadWriteLock.write();
            }
        },"tWrite1");

        Thread tWrite2 = new Thread(new Runnable() {
            public void run() {
                testReadWriteLock.write();
            }
        },"tWrite2");

        long start1 = System.currentTimeMillis();
        tRead1.start();
        tRead2.start();
        try {
            tRead1.join();
            tRead2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end1 = System.currentTimeMillis() - start1;
        System.out.println("读写锁读读重入；所以耗时 3000 毫秒");
        System.out.println("读读线程共耗时:" + end1);

//        long start2 = System.currentTimeMillis();
//        tRead1.start();
//        tWrite1.start();
//        try {
//            tRead1.join();
//            tWrite1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        long end2 = System.currentTimeMillis() - start2;
//        System.out.println("读写锁读写互斥；所以耗时 6000 毫秒");
//        System.out.println("读写线程共耗时:" + end2);

//        long start3 = System.currentTimeMillis();
//        tWrite1.start();
//        tWrite2.start();
//        try {
//            tWrite1.join();
//            tWrite2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        long end3 = System.currentTimeMillis() - start3;
//        System.out.println("读写锁写写互斥；所以耗时 6000 毫秒");
//        System.out.println("写写线程共耗时:" + end3);
    }
}


 /*

 log

占用 读取锁；当前线程:tRead1进入 ...
占用 读取锁；当前线程:tRead2进入 ...
释放 读取锁；当前线程:tRead2退出 ...
释放 读取锁；当前线程:tRead1退出 ...
读写锁读读重入；所以耗时 3000 毫秒
读读线程共耗时:3001

 ===

占用 读取锁；当前线程:tRead1进入 ...
释放 读取锁；当前线程:tRead1退出 ...
占用 写入锁；当前线程:tWrite1进入 ...
释放 写入锁；当前线程:tWrite1退出 ...
读写锁读写互斥；所以耗时 6000 毫秒
读写线程共耗时:6006

 ====
 占用 写入锁；当前线程:tWrite1进入 ...
释放 写入锁；当前线程:tWrite1退出 ...
占用 写入锁；当前线程:tWrite2进入 ...
释放 写入锁；当前线程:tWrite2退出 ...
读写锁写写互斥；所以耗时 6000 毫秒
写写线程共耗时:6002


 */