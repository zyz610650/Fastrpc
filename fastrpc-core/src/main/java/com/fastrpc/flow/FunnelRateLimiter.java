package com.fastrpc.flow;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 漏斗限流算法
 *
 * */
public class FunnelRateLimiter implements RateLimiter{
    private Map<String, Funnel> funnelMap = new ConcurrentHashMap<>();
    private int DEFAULT_CAPACITY=100;
    private float DEFAULT_RATE=10;

    @Override
    public synchronized boolean addTask(TaskParameter taskParameter) {

        return true;
    }

    @Override
    public TaskResult doTask(TaskParameter taskParameter, Task task) {
        long interval = taskParameter.getInterval();
        TimeUnit timeUnit = taskParameter.getTimeUnit();
        int num = taskParameter.getNum();
        float rate=(float) timeUnit.toSeconds(interval)/num;
        Funnel funnel=funnelMap.computeIfAbsent(taskParameter.getTaskName(),key->new Funnel(rate));
        // 超过漏斗限制
        if (!funnel.addTask(task))
            return new TaskResult(false,OVERRATE);
        funnel.doTask();
        return null;
    }


    public class Funnel{

        int capacity=DEFAULT_CAPACITY;// 漏斗容量 默认100

        float rate=DEFAULT_RATE; //水流速率 默认qps为 10

        int leftCapacity=DEFAULT_CAPACITY; // 剩余容量

        BlockingDeque<Task> taskBlockingDeque; // 任务队列



        public Funnel(int capacity, float rate) {
            this.capacity = capacity;
            this.rate = rate;
            taskBlockingDeque=new LinkedBlockingDeque<>(capacity);
        }
        public Funnel(float rate) {
           this(DEFAULT_CAPACITY,rate);
        }

        /**
         * 添加任务 如果漏斗满 则返回false 不阻塞
         * @param task
         */
        public boolean addTask(Task task)
        {
            return taskBlockingDeque.offer(task);
        }

        /**
         * 获取不到任务则阻塞
         * @return
         */
        public Task getTask()
        {
            try {
                return taskBlockingDeque.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void doTask()
        {
            new Thread(()->{
                Task task=getTask();
                Object res=task.doTask();
                // 保护性暂停模式
            });

        }
    }
}