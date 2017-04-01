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
t1 线程添加10个元素到list 数组中；
t2 线程死循环检测，当添加到list.size=5时候t2线程，执行某些指令，抛出错误终止线程

*/
public class TestThreadWaitNotify1 {

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
                "== 测试线程通信 1 ==");
        System.out.println(ExampleInfo);

         // Error:(53, 25) java: 从内部类中访问本地变量testThreadWaitNotify1; 需要被声明为最终类型
         //TestThreadWaitNotify1 testThreadWaitNotify1 = new TestThreadWaitNotify1();

        final TestThreadWaitNotify1 testThreadWaitNotify1 = new TestThreadWaitNotify1();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("线程t1 启动");
                    for (int i = 0; i<10; i++) {
                        testThreadWaitNotify1.addItem();
                        System.out.println("当前线程:" + Thread.currentThread().getName() + " 添加了一个元素");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                System.out.println("线程t2 启动");
                while (true) {
                    if (testThreadWaitNotify1.size() == 5) {
                        System.out.println("当前线程收到了通知：" + Thread.currentThread().getName() + " liset size=" + testThreadWaitNotify1.size() + "线程停止");
                        throw new RuntimeException();
                    }
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
 == 测试线程通信 1 ==

线程t2 启动
线程t1 启动
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
Exception in thread "t2" java.lang.RuntimeException
当前线程收到了通知：t2 liset size=5线程停止
	at Test07.TestThreadWaitNotify1$2.run(TestThreadWaitNotify1.java:80)
	at java.lang.Thread.run(Thread.java:745)
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素
当前线程:t1 添加了一个元素

Process finished with exit code 0

*/
