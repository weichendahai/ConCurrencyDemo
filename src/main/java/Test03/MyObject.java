package Test03;

/*

1.3 对象锁的同步和异步

同步: synchronized
同步的概念就是共享，共享，共享；如果不是共享的资源，就没必要进行同步

异步: asynchronized
异步的概念就是独立，独立，独立；相互之间不受到任何制。 就像Web 开发中的 ajax 技术


同步的目的就是为了线程安全；对于现象安全，需要满足两个特性:
原子性（同步）
可见性

实例说明

1.A线程先持有 Object对象的Lock锁，B线程如果在这个时候调用对象的中的同步方法（synchnoized）则需要等待，就是同步
2.A线程先持有 Object对象的Lock锁，B线程可以以异步方式调用对象中的 非同步方法（非synchronize 修饰的方法）

 */

/**
 * Created by weichen on 17/3/27.
 */


/*
对象锁的同步和异步问题
*/
public class MyObject {

//    同步方法 synchronized（需要等待对象锁；获取到对象锁，可执行)
    public synchronized void method1 () {
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 同步方法 synchronized（需要等待对象锁；获取到对象锁，可执行)
    public synchronized void method2 () {
        try {
            System.out.println(Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //   非同步方法 (不需要等待对象锁；因为无synchnoized 修饰；所以直接运行)
    public void method3 () {
        try {
            System.out.println(Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final MyObject myObject = new MyObject();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                myObject.method1();
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                myObject.method2();
            }
        },"t2");

        Thread t3 = new Thread(new Runnable() {
            public void run() {
                myObject.method3();
            }
        },"t3");

        t1.start();
        t2.start();
        t3.start();
    }
}

/*
线程输出信息
t1
t3
t2
*/

