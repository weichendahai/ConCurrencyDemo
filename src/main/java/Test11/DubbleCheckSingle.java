package Test11;

import java.util.Arrays;
import java.util.List;

/**
 * Created by weichen on 17/3/30.
 */

/*

 多线程模式下；单例模式
 经常使用两种方式

 dubble check instance （两次检查模式）
 static inner class （内部静态类方法）

*/
public class DubbleCheckSingle {

    private static DubbleCheckSingle ds;

    public static DubbleCheckSingle getInstance () {
        if (ds == null) {

            try {
                //初始化对象需要两秒时间
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //添加一把类锁
            synchronized (DubbleCheckSingle.class){
                if (ds == null) {
                    ds = new DubbleCheckSingle();
                }
            }
        }
        return ds;
    }

    public static void main(String[] args) {

        List<String> list = Arrays.asList("Hello", "World!");
       // list.stream().forEach(System.out::println);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println(DubbleCheckSingle.getInstance().hashCode());
            }
        },"t1");
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                System.out.println(DubbleCheckSingle.getInstance().hashCode());
            }
        },"t2");
        Thread t3 = new Thread(new Runnable() {
            public void run() {
                System.out.println(DubbleCheckSingle.getInstance().hashCode());
            }
        },"t3");

        t1.start();
        t2.start();
        t3.start();
    }

}

/*
log

42798055
42798055
42798055

Process finished with exit code 0
*/
