package com.fastrpc.flow;

public class LimitRateServiceImpl implements LimitRateService{

    FlowRating flowRating=new SlidingWindowFlowRating();

    /**
     * 方法级别限流
     * @param flowTask
     * @return
     */
    @Override
    public boolean isOver(FlowTask flowTask) {
        return flowRating.addTask(flowTask);
    }
}
