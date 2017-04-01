package Test09;

import Test03.MyObject;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by weichen on 17/3/29.
 */
public class MyQueue {

    private final LinkedList<Object> list = new LinkedList<Object>();

    //多线程操作，此处使用原子性变量作为 计数器
     private final AtomicInteger count = new AtomicInteger(0);

    private final int maxSize;
    private final int minSize = 0;

    private final Object lock = new Object();

    public MyQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    //添加元素到容器queue，如果容器到达最大个数，阻塞线程，阻断进入，直至容器queue里面有空间，在尽心添加
    public void put (Object obj) {
        synchronized (lock) {

            //当容器queue存储满了；线程属于等待状态；阻塞住队列，不在添加元素
            while (count.get() == maxSize) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //添加对象，计数器加1， 释放lock锁
            list.add(obj);
            count.getAndIncrement();
            System.out.println(" 元素: " + obj + " 被添加");
            lock.notify();
        }
    }

    //消费Queue 内排在首位的元素对象，若容器queue为空，阻塞线程，阻断进入，处于等待状态
    //当接收到put 方法的通知消息，再次被唤醒，进行消费
    public Object take () {
        Object temp = null;

        synchronized (lock) {

            //获取数据时，当发现容器queue 为空时候，阻塞当前进程，并且释放锁
            while (count.get() == minSize) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //计数器减1，获取容器queue内对象,释放锁
            count.getAndDecrement();
            temp = list.removeFirst();
            System.out.println(" 元素: " + temp + " 被消费");
            lock.notify();
        }

        return temp;
    }

    public int size () {
        return count.get();
    }

    public static void main(String[] args) {
        final MyQueue queue = new MyQueue(5);
        queue.put("a");
        queue.put("b");
        queue.put("c");
        queue.put("d");
//        queue.put("e");

        System.out.println("容器当前元素个数:" +  queue.size());

        Thread t1 = new Thread(new Runnable() {
            public void run() {

                try {
                    queue.put("h");
                    queue.put("i");
                    queue.put("g");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, "t1");


        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    queue.take();
                    queue.take();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t2");


        t1.start();
        t2.start();

        System.out.println("容器当前元素个数:" +  queue.size());
    }
}
