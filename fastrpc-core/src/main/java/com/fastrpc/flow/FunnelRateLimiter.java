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
    private long TIMEEOUT=100000;  //默认超时时间

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
        // 保护性暂停模式
        GuardedObject guardedObject=new GuardedObject(task);
        // 超过漏斗限制
        if (!funnel.addGuardedObject(guardedObject))
            return new TaskResult(false,OVERRATE);
        funnel.doTask();
        return guardedObject.getResult(TIMEEOUT);
    }


    public class Funnel{

        int capacity=DEFAULT_CAPACITY;// 漏斗容量 默认100

        float rate=DEFAULT_RATE; //水流速率 默认qps为 10 每rate秒执行一次任务

//        int leftCapacity=DEFAULT_CAPACITY; // 剩余容量

        BlockingDeque<GuardedObject> taskBlockingDeque; // 任务队列



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
         * @param guardedObject
         */
        public boolean addGuardedObject(GuardedObject guardedObject)
        {
            return taskBlockingDeque.offer(guardedObject);
        }

        /**
         * 获取不到任务则阻塞
         * @return
         */
        private GuardedObject getGuardedObject()
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
                GuardedObject guardedObject=getGuardedObject();
                while (guardedObject.isOverTime()) guardedObject=getGuardedObject();
                Task task = guardedObject.getTask();
                Object res = task.doTask();
                // 保护性暂停模式
                guardedObject.complet(res);

            });
            try {
                Thread.sleep((long) rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}