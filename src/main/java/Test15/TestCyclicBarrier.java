package Test15;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by weichen on 17/4/1.
 */

//5、CyclicBarrier和CountDownLatch的区别
/*
 两个看上去有点像的类，都在java.util.concurrent下，都可以用来表示代码运行到某个点上，二者的区别在于：
（1）CyclicBarrier的某个线程运行到某个点上之后，该线程即停止运行，直到所有的线程都到达了这个点，所有线程才重新运行；
         CountDownLatch则不是，某线程运行到某个点上之后，只是给某个数值-1而已，该线程继续运行
（2）CyclicBarrier只能唤起一个任务，CountDownLatch可以唤起多个任务
（3）CyclicBarrier可重用，CountDownLatch不可重用，计数值为0该CountDownLatch就不可再用了
 */

/*
代码说明
创建一个 TestCyclicBarrier，然后3个一组执行；
创建一个固定个数线程池，然后运行线程
*/
public class TestCyclicBarrier implements Runnable{
    private CyclicBarrier barrier;
    private String name;

    public TestCyclicBarrier (CyclicBarrier barrier, String name) {
        this.barrier = barrier;
        this.name = name;
    }

    public void run() {
        try {
            System.out.println(name + " 开始准备");
            Thread.sleep(1000);
            System.out.println(name + " 准备OK");
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println(name + " Go Go !!");
    }

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        //三个一组出发；当有三个到期，一同出发
        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "beijing_01")));
        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "tianjin_01")));
        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "shanghai_01")));

        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "beijing_02")));
        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "tianjin_02")));
        threadPool.submit(new Thread(new TestCyclicBarrier(barrier, "shanghai_02")));

        // 退出线程池
        threadPool.shutdown();
    }
}