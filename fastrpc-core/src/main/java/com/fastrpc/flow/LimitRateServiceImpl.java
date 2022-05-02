package com.fastrpc.flow;

public class LimitRateServiceImpl implements LimitRateService{

    RateLimiter flowRating=new SlidingWindowRateLimiter();

    /**
     * 方法级别限流
     * @param taskParameter
     * @return
     */
    @Override
    public boolean isOverByMethod (TaskParameter taskParameter) {
        return true;
//        return flowRating.addTask(taskParameter);
    }
}
