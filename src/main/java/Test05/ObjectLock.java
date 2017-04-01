package Test05;

/**
 * Created by weichen on 17/3/29.
 */
public class ObjectLock {
    
    //对象锁；实例出来的对象加锁；并不是类锁
    public void method1 () {
        synchronized (this) {
            System.out.println("do method 01");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //类锁；直接 synchronized 类；类
    public void method2 () {
        synchronized (ObjectLock.class) {
            System.out.println("do method 02");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //任何对象锁
    private Object lock = new Object();
    public void method3 () {
        synchronized (lock) {
            System.out.println("do method 03");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
