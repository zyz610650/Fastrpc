package com.fastrpc.flow;

public interface LimitRateService {

    public boolean isOver(FlowTask flowTask);

}
