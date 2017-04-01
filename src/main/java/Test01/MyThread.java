package Test01;

/**
 * Created by weichen on 17/3/27.
 */
public class MyThread extends Thread {

    private int count = 0;

//    修饰一个代码块
//    一个线程访问一个对象中的synchronized(this)同步代码块时，其他试图访问该对象的线程将被阻塞

//    说明：
//    synchronized  是java 中的关键字；是一中同步锁。它修饰的对象有一下几种
//    1. 修饰一个代码段： 被修饰的代码块称为同步语句块；其作用范围是大括号{} 阔起来的代码；作用对象是调用这个代码块的对象
//    2. 修饰一个方法：   被修饰的方法，称为 同步方法，其作用范围是这个方法，租用对象是整个调用这个方法的对象
//    3. 修饰一个静态方法：其作用的范围是整个静态方法，作用的对象是这个类的所有对象；
//    4. 修饰一个类：其作用的范围是synchronized后面括号括起来的部分，作用主的对象是这个类的所有对象。

    //    线程安全的写法使用 synchronized 同步锁，保证只有线程拿到方法锁之后；才可以执行；否则只能排队
//    重写 thread run 方法
    @Override
    public synchronized void run() {
        this.count = this.count + 1;
        System.out.println(this.currentThread().getName() + " count is :" + count);
    }

//    @Override
//    public void run () {
//        synchronized (this) {
//            this.count = this.count + 1;
//            System.out.println(this.currentThread().getName() + " count is :" + count);
//        }
//    }

//      非线程安全的写法
//      重写 thread run 方法
//    @Override
//    public void run () {
//        this.count = this.count + 1;
//        System.out.println(this.currentThread().getName() + " count is :" + count);
//    }

    public static void main(String[] args) {

        //实例化一个对象
        MyThread myThread = new MyThread();

        //根据 myThread 对象，创建多个线程；线程运行
        Thread t1 = new Thread(myThread, "t1");
        Thread t2 = new Thread(myThread, "t2");
        Thread t3 = new Thread(myThread, "t3");
        Thread t4 = new Thread(myThread, "t4");
        Thread t5 = new Thread(myThread, "t5");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}

/*
多线程 安全 输出
/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/bin/jav...
 t1 count is :1
 t5 count is :2
 t4 count is :3
 t3 count is :4
 t2 count is :5
*/

/*
非线程 不安全 输出
/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/bin/jav...

t1 count is :2
t4 count is :4
t3 count is :3
t2 count is :2
t5 count is :5
*/