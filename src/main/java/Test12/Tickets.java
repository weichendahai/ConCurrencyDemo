package Test12;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by weichen on 17/3/30.
 */
public class Tickets {

    public static void main(String[] args) {

//        final Vector<String> tickets = new Vector<String>();
//        final List<String> tickets = Collections.synchronizedList(new ArrayList<String>()) ;
        final List<String> tickets = Collections.synchronizedList(new ArrayList<String>()) ;
        for (int i=0; i<=1000; i++) {
            tickets.add("火车票_" + i);
        }

//        Object lock = new Object();
        for (int i =0; i<10; i++) {
//            synchronized (lock) {
                new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            if (tickets.isEmpty()) break;

                            System.out.println(Thread.currentThread().getName() + " ----- " + tickets.remove(0));
                        }
                    }
                },"线程_"+i).start();
//            }
        }
    }

}
