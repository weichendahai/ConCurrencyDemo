package Test04;


//示例说明
//在我们对一个对象的方法枷锁的时候，需要考虑整体性；
//即为setValue和getValue方法同时加锁synchronized同步关键字,
//保证业务（service）原子性；不然会出现业务错误（从侧面保证业务一致性）

/**
 * Created by weichen on 17/3/27.
 */
public class DirtyRead {
    private String userName = "lili";
    private String passwd = "123456";

    public synchronized void setValue (String userName, String passwd) {

        this.userName = userName;
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.passwd = passwd;

        System.out.println("setValue 结果为: " + "userName is: " + this.userName + ", passwd is : " + passwd);
    }

    public synchronized void getValue () {
        System.out.println("getValue 结果为: " + "userName is: " + this.userName + ", passwd is : " + passwd);
    }

    public void getValueNoSafe () {
        System.out.println("getValue 结果为: " + "userName is: " + this.userName + ", passwd is : " + passwd);
    }

    public static void main(String[] args) {

        final DirtyRead dirtyRead = new DirtyRead();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                dirtyRead.setValue("lilei", "i beijing");
            }
        });

        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dirtyRead.getValueNoSafe();

        dirtyRead.getValue();
    }
}

/*
示例输出
getValue 结果为: userName is: lilei, passwd is : 123456
setValue 结果为: userName is: lilei, passwd is : i beijing
getValue 结果为: userName is: lilei, passwd is : i beijing
*/
