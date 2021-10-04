package com.fastrpc.extension;

/**
 * @author: @zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
