package Test02;


/*
* 1.2 过个线程过个锁
*  多个线程多个锁：多个线程，每个线程都可以拿到自己指定的锁；分别获得锁之后，执行synchnoized 方法体内容
*
*
* 示例2 multi thread
*
* 示例说明：
* 关键字synchnoize 取得的琐是对象锁，而不是把一段代码（方法）当做锁
* 所以，示例代码中哪个线程先执行synchnoized关键字的方法，哪个线程就持有该方法所属对象的锁（Lock）
* 实例两个对象，线程获得的就是两个不同的锁，他们之间互不影响
*
* 注释：
* 1.有一种情况则是相同锁，及在静态方法上加synchnoized 关键字，表示锁定.class类
*   类一级别的锁（独占.class 类）
*
*
* */

/**
 * Created by weichen on 17/3/27.
 */
//public class MultThread extends Thread {
public class MultThread {


    private static int num = 0;

    /*static*/
    public static synchronized void printNum(String tag) {
        try {
            if ("a".equals(tag)) {
                num = 100;
                System.out.println("tag a, set num over");
                Thread.sleep(1000);
            } else {
                num = 200;
                System.out.println("tag b, set num over");
            }
            System.out.println("tag is :" + tag + "; num is :" + num);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {

        //创建两个不同的对象；那么就同时产生 连个对象锁；
        //但是线程内；使用 static synchronized 形成类锁；所以线程安全，只是效率低下
        final MultThread mt1 = new MultThread();
        final MultThread mt2 = new MultThread();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                mt1.printNum("a");
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                mt2.printNum("b");
            }
        });

        t1.start();
        t2.start();
    }
}


/*
线程安全输出
tag a, set num over
tag is :a; num is :100
tag b, set num over
tag is :b; num is :200
 */