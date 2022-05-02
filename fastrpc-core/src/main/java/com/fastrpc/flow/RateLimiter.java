package com.fastrpc.flow;

public interface RateLimiter {

    String OVERRATE="The server is under too much pressure, please try again later.";
    /**
     * 任务添加成功则可以运行
     * @param  taskParameter
     * @return
     */
     boolean addTask(TaskParameter taskParameter);


     TaskResult doTask(TaskParameter taskParameter,Task task);


}
