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
    private float DEFAULT_RATE=1000;
    private long TIMEEOUT=1000;  //默认超时时间 毫秒
    private Object OBJ_COMPLETE=new Object();

    @Override
    public synchronized boolean addTask(TaskParameter taskParameter) {

        return true;
    }

    @Override
    public TaskResult doTask(TaskParameter taskParameter, Task task) {
        long interval = taskParameter.getInterval();
        TimeUnit timeUnit = taskParameter.getTimeUnit();
        int num = taskParameter.getNum();
        float rate=((float) timeUnit.toSeconds(interval)/num)*1000;
        Funnel funnel=funnelMap.computeIfAbsent(taskParameter.getTaskName(),key->new Funnel(rate));
        // 保护性暂停模式
        GuardedObject guardedObject=new GuardedObject(task);
        // 超过漏斗限制
        if (!funnel.addGuardedObject(guardedObject))
            return new TaskResult(false,OVERRATE);
        Task taskRes= guardedObject.getResult(TIMEEOUT);
        if (taskRes==null)
        {
            return new TaskResult(false,OVERRATE);
        }

        Object res= taskRes.doTask();
        return new TaskResult(res);
    }


    public class Funnel{

        int capacity=DEFAULT_CAPACITY;// 漏斗容量 默认100

        float rate=DEFAULT_RATE; //水流速率 默认qps为 1 每rate秒执行一次任务

//        int leftCapacity=DEFAULT_CAPACITY; // 剩余容量

        BlockingDeque<GuardedObject> taskBlockingDeque; // 任务队列

        private Thread taskThread;



        public Funnel(int capacity, float rate) {
            this.capacity = capacity;
            this.rate = rate;
            taskBlockingDeque=new LinkedBlockingDeque<>(capacity);
            // 开始处理任务
            doTask();
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

        public  void doTask()
        {
            //
            if (taskThread==null) {
                taskThread = new Thread(() -> {
                    while (true) {

                        GuardedObject guardedObject = getGuardedObject();
                        // 获取不到线程回阻塞
                        while (guardedObject.isOverTime()) guardedObject = getGuardedObject();
                        // 保护性暂停模式
                        guardedObject.complet(OBJ_COMPLETE);
                        try {
                            Thread.sleep((long) (rate));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                taskThread.start();
            }


        }
    }
}