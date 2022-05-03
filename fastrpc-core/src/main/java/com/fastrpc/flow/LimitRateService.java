package com.fastrpc.flow;

public interface LimitRateService {

    public TaskResult doMethod(RateLimiter rateLimiter,TaskParameter taskParameter, Task task);

}
