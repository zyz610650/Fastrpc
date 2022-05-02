package com.fastrpc.enums;

public enum LimitMethod {

    /**
     * 漏斗限流算法
     */
    FUNNELRATE("com.fastrpc.flow.FunnelRateLimiter"),
    /**
     * 滑动窗口限流算法
     */
    SLIDINGWINDOWRATE("com.fastrpc.flow.SlidingWindowRateLimiter"),

    /**
     * 令牌桶限流算法
     */
    TOKENBUCKETRATE("com.fastrpc.flow.TokenBucketRateLimiter");
   private String value;

    LimitMethod(String value) {
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
