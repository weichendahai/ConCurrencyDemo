package Test13.MasterWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weichen on 17/3/31.
 */
public class Master {
    //1 有一个盛放任务的容器
    //2 需要有一个盛放worker的集合
    //3 需要有一个盛放每一个worker执行任务的结果集合
    //4 构造方法
    //5 需要一个提交任务的方法
    //6 需要有一个执行的方法，启动所有的worker方法去执行任务
    //7 判断是否运行结束的方法
    //8 计算结果方法

    //1 定义一个存储任务的容器(有可能是多线程添加任务)
    private ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();

    //2 定义一个存储worker集合方法
    private HashMap<String, Thread> workers = new HashMap<String, Thread>();

    //3 定义结果，存储每个work 执行任务的结果集合 （可能多线程同时写入）
    private ConcurrentHashMap<String,Object> resultMap = new ConcurrentHashMap<String, Object>();

    public Master(Worker worker, int workerCount) {

        //master 构造函数时候，设定消费者，及消费者队列
        worker.setTaskQueue(this.taskQueue);
        worker.setResultMap(this.resultMap);

        for (int i=0; i<workerCount; i++) {
            this.workers.put(Integer.toString(i), new Thread(worker));
        }
    }

    //保存数据到master 的任务容器中
    public void submit (Task task) {
        this.taskQueue.add(task);
    }

     //启动master 控制的所有消费者（work）对象
    public void execute () {
        for (Map.Entry<String ,Thread> me : workers.entrySet()){
            me.getValue().start();
        }
    }

//    判断是否消费完成
    public boolean isComplete() {
        for (Map.Entry<String,Thread> me : workers.entrySet()) {
            if (me.getValue().getState() != Thread.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

//    计算结果
    public int getResult () {
        int priceResult = 0;
        for (Map.Entry<String,Object> me : resultMap.entrySet()) {
            priceResult += (Integer) me.getValue();
            //System.out.println("处理过程结果:" + me.getValue());
        }
        return priceResult;
    }
}



