package com.fastrpc.flow;

public class GuardedObject {

    // 唯一标识当前执行的任务
   private Task task;
   // 返回的结果
   private Object res;

   // 标记是否超时
   private boolean isOverTime=false;

    String OVERRATE="The server is under too much pressure, please try again later.";
    public GuardedObject(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Task getResult(long timeout)
    {
       synchronized (this)
       {
           long startTime=System.currentTimeMillis();
           long waitTime=timeout;
           while(res==null)
           {

               if (waitTime>0)
               {
                   // 虚假唤醒
                   try {
                       this.wait(waitTime);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }else{
                    isOverTime=true;
                  return null;// 等待超时
               }
               long cntTime=System.currentTimeMillis();
               waitTime=timeout-(cntTime-startTime);
           }
       }
        return task;
    }
    public boolean isOverTime()
    {
        return isOverTime;
    }
    public void complet(Object res)
    {
        synchronized (this)
        {
            this.res=res;
            this.notifyAll();
        }
    }
}
