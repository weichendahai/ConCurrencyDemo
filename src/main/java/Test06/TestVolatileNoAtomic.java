package Test06;
/**
 * Created by weichen on 17/3/29.
 */

/*
volatile 只能保证可见，不能保证原子性
*/
public class TestVolatileNoAtomic extends Thread{

    private static volatile int count;
    //private static int count;

    private void addCount () {
        for (int i=0; i< 1000 ; i++) {
            count = count + 1;
        }
        System.out.println("count is: " + count);
    }

//    private static AtomicInteger  countAtomic = new AtomicInteger(0);
//    private void addCountAtomic () {
//        for (int i=0; i< 1000 ; i++) {
//            //count = count + 1;
//            countAtomic.incrementAndGet();
//        }
//        System.out.println("count atomic is: " + countAtomic);
//    }

    @Override
    public void run () {
        addCount();
    }

    public static void main(String[] args) {

        TestVolatileNoAtomic[] arrTest = new TestVolatileNoAtomic[10];

        for (int i=0; i<10; i++) {
            arrTest[i] = new TestVolatileNoAtomic();
        }

        for (int i=0; i<10; i++) {
            arrTest[i].start();
        }
    }
}

/*
#非原子性 log
count is: 2000
count is: 4189
count is: 3189
count is: 3189
count is: 6189
count is: 2671
count is: 5189
count is: 8189
count is: 7189
count is: 9189

*/
