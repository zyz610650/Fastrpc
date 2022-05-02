package com.fastrpc.flow;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/liyantianmin/article/details/79086571
// https://baijiahao.baidu.com/s?id=1677003466082843718&wfr=spider&for=pc
// 使用guava 提供的RateLimiter
@Slf4j
public class TokenBucketFlowLimiter implements com.fastrpc.flow.RateLimiter {


    private volatile Map<String,RateLimiter> rateCache=new ConcurrentHashMap<>();

    public TokenBucketFlowLimiter() {
    }


    @Override
    public boolean addTask(TaskParameter taskParameter) {
        TimeUnit timeUnit = taskParameter.getTimeUnit();
        long seconds=timeUnit.toSeconds(taskParameter.getInterval());
        double rate= seconds/ taskParameter.getNum();
        RateLimiter rateLimiter=rateCache.computeIfAbsent(taskParameter.getTaskName(),key->RateLimiter.create(rate));

        if (rateLimiter.tryAcquire())
        {
            rateLimiter.acquire();
            log.info(" Get a token from bucket");
            return true;
        }else    return false;
    }

    @Override
    public TaskResult doTask(TaskParameter taskParameter, Task task) {

        if (addTask(taskParameter))
        {
            Object res=task.doTask();
            return new TaskResult(res);
        }
        return new TaskResult(false, OVERRATE);

    }
}
