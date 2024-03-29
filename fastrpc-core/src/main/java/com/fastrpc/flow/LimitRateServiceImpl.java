package com.fastrpc.flow;

import com.fastrpc.extension.SPI;


public class LimitRateServiceImpl implements LimitRateService{

    //
//    private static RateLimiter methodFlowRating=new SlidingWindowRateLimiter();

    /**
     * 方法级别限流
     * @param taskParameter
     * @param task
     * @return
     */
    @Override
    public TaskResult doMethod(RateLimiter rateLimiter,TaskParameter taskParameter, Task task) {
        return rateLimiter.doTask(taskParameter,task);

    }
}
