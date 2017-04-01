package Test06;

/**
 * Created by weichen on 17/3/29.
 */

/*
#volatile
## 概念
volatile 作用使得变量在多个线程可见，但是不具备原子性

## 示例说明

 在java中，每一个线程都会有一块工作内存区，其中存放所有线程共享的主内存中的变量值的拷贝，
 当线程执行时，他在自己的工作内存中操作这些变量。为了存取一个共享的变量，一个线程通常先获取锁定，并去清除他的内存工作区，
 把这些共享变量从所有线程的共享内存中正确的装入到他自己所在的工作内存中，
 当线程解锁时，保证该工作内存中变量的值会回到共享内存中。

 一个线程可以执行的操作
 使用（use），赋值（assign），装载（load），存储（store），锁定（lock），解锁（unlock）

 而朱内存可以执行的操作有
 读（read），写（write），锁定（lock），解锁（unlock）每个操作都是原子性的


 volatile 的作用就是，强制线程到主内存（共享内存）里读取变量。而不去线程工作内存区去读取，
 从而实现多线程间变量可见。也就是满足线程安全的可见性
*/

/*
例子说明：
创建类 TestVolatile 实现Runnable；内部run 死循环输出数据；
直至isRunning被修改为false，退出循环;

main函数中新建两个线程；然后在main线程修改isRunning=false;
观察其他连个线程是否结束
*/
public class TestVolatile implements Runnable{

    private volatile static boolean isRunning = true;   //修改isRunning=false,所有线程都能接收到
    //public volatile  boolean isRunning = true;        //修改isRunning=false,当前线程和主线程 都能接收到
    //private  boolean isRunning = true;                //修改isRunning=false,只有当前线程都能接收到

    private String name ;

    public TestVolatile(String name) {
        this.name = name;
    }

    private void setRunning(boolean running) {
        this.isRunning = running;
    }

    private boolean getRunning() {
        return isRunning;
    }

    public void run () {
        System.out.println("进入run 方法" + this.name);
        while (isRunning == true) {
            //... ...
            try {
                Thread.sleep(1000);
                System.out.println(this.name + "; isRuning="+ this.isRunning);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("线程结束停止"  + this.name);
    }

    public static void main(String[] args) {
        TestVolatile testVolatile01 = new TestVolatile("Thread01");
        TestVolatile testVolatile02 = new TestVolatile("Thread02");

        new Thread(testVolatile01).start();
        new Thread(testVolatile02).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testVolatile01.setRunning(false);

        System.out.println("isRunning 已经被修改为 false");

        try {
            Thread.sleep(1000);
            System.out.println("isRunning  is=" + testVolatile01.isRunning);
            System.out.println("isRunning2 is=" + testVolatile02.isRunning);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*

#说明
如果变量isRunning不带有volatile修饰；进程无法结束
因为，主进程修改的主进程内isRunning值，而子线程读取的是 自己线程内isRunning
如果添加volatile修饰，强制进程去住进程读取变量

#log（无volatile）
进入run 方法
isRunning 已经被修改为 false
isRunning is=false


#log (含有volatile)
进入run 方法
isRunning 已经被修改为 false
线程结束停止
isRunning is=false

*/

/*
log

进入run 方法Thread01
进入run 方法Thread02
Thread02; isRuning=true
Thread01; isRuning=true
Thread01; isRuning=true
Thread02; isRuning=true
isRunning 已经被修改为 false
Thread01; isRuning=false
线程结束停止Thread01
Thread02; isRuning=false
线程结束停止Thread02
isRunning  is=false
isRunning2 is=false
*/