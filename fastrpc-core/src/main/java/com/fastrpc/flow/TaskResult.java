package com.fastrpc.flow;

import javax.annotation.Nullable;

/**
 * 任务执行结果
 */
public class TaskResult {
    private boolean isSuccess;
    private Object res;
    //异常结果
    @Nullable
    private String ExceptionResult;

    public TaskResult(boolean isSuccess,  String exceptionResult) {
        this.isSuccess = isSuccess;
        ExceptionResult = exceptionResult;
    }

    public TaskResult(Object res) {
        isSuccess=true;
        this.res = res;
    }


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }

    @Nullable
    public String getExceptionResult() {
        return ExceptionResult;
    }

    public void setExceptionResult(@Nullable String exceptionResult) {
        ExceptionResult = exceptionResult;
    }
}
