package Test05;


//synchnoized 重入锁；
//
//synchronized 拥有重入锁的功能；也就是当使用synchronized 时，
//当一个线程得到了一个对象的锁之后，再次请求此对象时，是可以再次获得该对象的锁


/**
 * Created by weichen on 17/3/27.
 */
public class SyncDubbo01 {

    public synchronized void method1 () {
        System.out.println("my is method 1 1 ... ");
        this.method2();
    }

    public synchronized void method2 () {
        System.out.println("my is method 2 2 ...");
        this.method3();
    }

    public synchronized void method3 () {
        System.out.println("my is method 3 3 ...");
    }

    public static void main(String[] args) {
        final SyncDubbo01 syncDubbo = new SyncDubbo01();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                syncDubbo.method1();
            }
        });

        t1.start();
    }
}
