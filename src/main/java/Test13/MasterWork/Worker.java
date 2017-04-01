package Test13.MasterWork;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weichen on 17/3/31.
 */
public class Worker implements Runnable{

    //定义一个待消费队列容器
    private ConcurrentLinkedQueue<Task> taskQueue;
    //定义一个存放消费结果容器
    private ConcurrentHashMap<String, Object> resultMap;

    public void setTaskQueue(ConcurrentLinkedQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setResultMap ( ConcurrentHashMap<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    //死循环消费；容器内任务
    public void run() {
        while (true) {
            Task input = this.taskQueue.poll();
            if (input == null) break;

            Object output = handle(input);
            this.resultMap.put(Integer.toString(input.getId()),output);
        }
    }

    //具体根据master容器内数据；进行处理然后返回处理结果
    private Object handle (Task task) {
        Object output = null;

        try {
            Thread.sleep(20);
            output = task.getPrice();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  output;
    }
}
