package com.fastrpc.service;

import com.fastrpc.annotation.Controller;
import lombok.SneakyThrows;

/**
 * @author yvioo
 */
public class MyRunnable implements Runnable {

    static Object object=new Object();
    @SneakyThrows
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" "+object);

        synchronized (object)
        {
            System.out.println(Thread.currentThread().getName()+"get lock ");
            Thread.sleep(50000000);
        }

    }
    @SneakyThrows
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread thread1=new Thread(myRunnable);
        Thread thread2=new Thread(myRunnable);
        thread1.start();
        Thread.sleep(1000);
        thread2.start();
    }
}
