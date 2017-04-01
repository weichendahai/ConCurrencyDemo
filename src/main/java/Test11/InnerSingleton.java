package Test11;

/**
 * Created by weichen on 17/3/30.
 */

/*

 多线程模式下；单例模式
 经常使用两种方式

 dubble check instance （两次检查模式）
 static inner class （内部静态类方法）

*/
public class InnerSingleton {

    private static class Singletion {
        private static Singletion singletion = new Singletion();
    }

    public static Singletion getInstance () {
        return Singletion.singletion;
    }

    public static void main(String[] args) {
        final InnerSingleton innerSingleton = new InnerSingleton();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println(InnerSingleton.getInstance().hashCode());
            }
        }, "t1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                System.out.println(InnerSingleton.getInstance().hashCode());
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
