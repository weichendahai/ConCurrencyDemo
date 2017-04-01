package Test13.MasterWork;

import java.util.Random;

/**
 * Created by weichen on 17/3/31.
 */
public class Main {
    public static void main(String[] args) {
        //1.实例化带有10个消费者的Master
        int workCount = 10;
        Master master = new Master(new Worker(),workCount);

        //2.循环创建100个任务到master的内部容器中

        int myPriceResult = 0;

        Random r = new Random();
        for (int i=1; i<=1000 ; i++) {
            int tempPrice = r.nextInt(1000);
            Task t = new Task();
            t.setId(i);
            t.setPrice(tempPrice);
            t.setName("name_" + i);
            myPriceResult += tempPrice;
            master.submit(t);
        }

        System.out.println("自己预计结果为:" + myPriceResult);

//        启动master内部刚刚创建的10个消费者
        master.execute();

        long start = System.currentTimeMillis();

        while(true){
            if(master.isComplete()){
                long end = System.currentTimeMillis() - start;
                int priceResult = master.getResult();
                System.out.println("最终结果：" + priceResult + ", 执行时间：" + end);
                break;
            }
        }
    }
}

/*
log 输出
自己预计结果为:506275
最终结果：506275, 执行时间：2293
*/