package Test02;

/*
*
* 1.2 非static 修饰的 synchronized；
* synchronized 对象锁
* static synchronized 类锁
*
* */

/**
 * Created by weichen on 17/3/27.
 */
public class MultiThreadNoSafe {

    private int num = 0;

    /*static*/
    public synchronized void printNum(String tag) {
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
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
    }

    public static void main(String[] args) {

        //创建两个不同的对象；
        final MultiThreadNoSafe mt1 = new MultiThreadNoSafe();
        final MultiThreadNoSafe mt2 = new MultiThreadNoSafe();

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
非线程安全输出
tag a, set num over
tag b, set num over
tag is :b; num is :200
tag is :a; num is :100
 */