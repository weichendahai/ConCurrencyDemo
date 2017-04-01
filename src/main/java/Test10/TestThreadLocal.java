package Test10;

/**
 * Created by weichen on 17/3/30.
 */
public class TestThreadLocal {
    public static ThreadLocal<String> th = new ThreadLocal<String>();

    public void getTh() {
        System.out.println("Thread Name:" + Thread.currentThread().getName() + "; TheadLocal is: " + this.th.get());
    }

    public void setTh(String value) {
        th.set(value);
    }


    public static void main(String[] args) {
        final TestThreadLocal testThreadLocal = new TestThreadLocal();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                testThreadLocal.setTh("张三");
                testThreadLocal.getTh();
            }
        }, "t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    testThreadLocal.setTh("李四");
                    testThreadLocal.getTh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}

/*
Thread Name:t1; TheadLocal is: 张三
Thread Name:t2; TheadLocal is: 李四

Process finished with exit code 0

*/
