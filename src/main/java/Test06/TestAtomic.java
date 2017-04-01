package Test06;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by weichen on 17/3/29.
 */

/*
java中如果想实现多线程原子性运算，建议使用atomic 类型；
volatile 只能保证可见，不能保证原子性;

注意：atomic类，只保证本身方法的原子性，并不保证多次操作的原子性
比如你在一个线程内多次执行atomic**.add**(1)


 示例说明：
 将数字count=1000，经过10个线程执行，每个线程给count递增1000；最终结果获得10000；
 前面线程打印可能有延迟，但是最后一个线程，最后打印硬顶是10000；因为无线程争抢

 问题点（1.多线程变量可见性； 2.多线程操作变量的原子性）
*/
public class TestAtomic extends Thread {

    private static AtomicInteger countAtomic = new AtomicInteger(0);

    private static void addCountAtomic () {
        for (int i=0; i< 1000 ; i++) {
            //count = count + 1;
            countAtomic.incrementAndGet();
        }
//        try {
//            Thread.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("thread is" + Thread.currentThread().getName() + "; count atomic is: " + countAtomic);
    }

    @Override
    public void run () {
        addCountAtomic();
    }

    public static void main(String[] args) {

        TestAtomic[] testAtomics = new TestAtomic[10];

        for (int i=0; i<10; i++) {
            testAtomics[i] = new TestAtomic();
        }

        for (int i=0; i<10; i++) {
            testAtomics[i].start();
        }
    }
}
