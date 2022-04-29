package com.fastrpc.flow;

public interface FlowRating {

    /**
     * 任务添加成功则可以运行
     * @param taskName
     * @return
     */
     boolean addTask(String taskName);


}
