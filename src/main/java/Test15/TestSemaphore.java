package Test15;

import java.util.concurrent.*;

/**
 * Created by weichen on 17/4/1.
 */
public class TestSemaphore {

    public static void main(String[] args) {
        // 线程池
        ExecutorService executorServicePoll = Executors.newCachedThreadPool();

        //只能5个线程同事访问
        final Semaphore semaphore = new Semaphore(5);


        //模拟客户端N个客户端
        for (int index = 0; index < 20; index++) {
            final int NO = index;

            Runnable run = new Runnable() {
                public void run() {

                    try {
                        //获取信号许可
                        semaphore.acquire();
                        System.out.println("Accessing: " + NO);
                        // 模拟业务；休息2s 线程
                        Thread.sleep(2000);
                        //访问后，释放
                        semaphore.release();
                        System.out.println("-----------------"+semaphore.availablePermits());



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            executorServicePoll.execute(run);
        }

        //休眠
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //退出线程池
        executorServicePoll.shutdown();
    }
}