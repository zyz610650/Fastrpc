package com.fastrpc.flow;

import java.util.concurrent.TimeUnit;

/**
 * TPS: timeStamp/rate 默认一秒一次
 */
public  class TaskParameter {

    // 类名+方法名
    private String taskName;

    /**
     * 时间单位 默认为秒
     */
    private TimeUnit timeUnit=TimeUnit.SECONDS;

    /**
     * interval时间间隔内允许处理 num个任务 interval也就是窗口大小
     */
    private long interval = 10;

    /**
     * 处理任务个数 10
     */
    private  int num=10;


    public TaskParameter(String taskName) {
        this.taskName = taskName;
    }

    public TaskParameter(String taskName, TimeUnit timeUnit, long interval, int num) {
        this.taskName = taskName;
        this.timeUnit = timeUnit;
        this.interval = interval;
        this.num = num;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("TPS parameter information: {");
        sb.append("taskName: ").append(taskName)
                .append("timeUnit: ")
        .append(timeUnit)
                .append(", interval: ")
                .append(interval)
                .append(", num: ")
                .append(num)
                .append(" }");
        return sb.toString();
    }


}
