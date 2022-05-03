package com.fastrpc.flow;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SlidingWindowRateLimiter implements RateLimiter {


    /** 队列id和队列的映射关系，队列里面存储的是每一次通过时候的时间戳，这样可以使得程序里有多个限流队列 */
    //  List<Long>就是滑动窗口
    private volatile  Map<String, List<Long>> rateCache = new ConcurrentHashMap<>();


    public SlidingWindowRateLimiter() {
    }

// 考虑线程安全问题
    @Override
    public synchronized boolean addTask(TaskParameter taskParameter) {
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        long timeWindow= taskParameter.getInterval();
        int count= taskParameter.getNum();
        // 根据队列id，取出对应的限流队列，若没有则创建
        List<Long> list = rateCache.computeIfAbsent(taskParameter.getTaskName(), key -> new LinkedList<>());
        // 如果队列还没满，则允许通过，并添加当前时间戳到队列开始位置
        if (list.size() < count) {
            list.add(0, nowTime);
            return true;
        }
        // 队列已满（达到限制次数），则获取队列中最早添加的时间戳
        Long farTime = list.get(count - 1);

        // 用当前时间戳 减去 最早添加的时间戳
        if (nowTime - farTime <= timeWindow*1000) {
            // 若结果小于等于timeWindow，则说明在timeWindow内，通过的次数大于count
            // 不允许通过

            return false;
        } else {
            // 若结果大于timeWindow，则说明在timeWindow内，通过的次数小于等于count
            // 允许通过，并删除最早添加的时间戳，将当前时间添加到队列开始位置
            list.remove(count - 1);
            list.add(0, nowTime);
            return true;
        }

    }

    @Override
    public TaskResult doTask(TaskParameter taskParameter, Task task) {
        if (addTask(taskParameter))
        {
            Object res=task.doTask();
            return new TaskResult(res);
        }
        return new TaskResult(false,OVERRATE);

    }

//    public static void main(String[] args) throws InterruptedException {
//        while (true) {
//            // 任意10秒内，只允许2次通过
////            System.out.println(LocalTime.now().toString() + SlidingWindowFlowRating.isGo("ListId", 2, 10000L));
//            // 睡眠0-10秒
//            Thread.sleep(1000 * new Random().nextInt(10));
//        }
//    }
}
