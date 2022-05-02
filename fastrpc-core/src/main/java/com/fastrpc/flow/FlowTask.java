package com.fastrpc.flow;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * TPS: timeStamp/rate 默认一秒一次
 */
public  class FlowTask {

    // 类名+方法名
    private String taskName;

    /**
     * 时间单位 默认为秒
     */
    private TimeUnit timeUnit=TimeUnit.SECONDS;

    /**
     * timeStamp时间内允许处理 num个任务  timeStamp也就是窗口大小
     */
    private long timeStamp = 10;

    /**
     * 处理任务个数 10
     */
    private  int num=10;


    public FlowTask(String taskName) {
        this.taskName = taskName;
    }

    public FlowTask(String taskName, @Nullable TimeUnit timeUnit,@Nullable long timeStamp, @Nullable int num) {
        this.taskName = taskName;
        this.timeUnit = timeUnit;
        this.timeStamp = timeStamp;
        this.num = num;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
        sb.append("timeUnit: ")
        .append(timeUnit)
                .append(", timeStamp: ")
                .append(timeStamp)
                .append(", num: ")
                .append(num)
                .append(" }");
        return sb.toString();
    }


}
