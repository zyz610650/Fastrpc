package com.fastrpc.transport.netty.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

// 这里由于 numConnections需要增加和删除 LongAdder不能提供获得和增加的原子性所以用 AtomicLong
// 而统计拒绝数量 不需要两种操作，并且LongAdder性能高 所以用LongAdder
@ChannelHandler.Sharable
public class RpcConnectionLimitHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(RpcConnectionLimitHandler.class);
    private final int maxConnectionNum;
    private final AtomicLong numConnections = new AtomicLong(0);
    private final LongAdder numDroppedConnections = new LongAdder();
    private final AtomicBoolean loggingScheduled = new AtomicBoolean(false);

    private final Set<Channel> childChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public RpcConnectionLimitHandler(int maxConnectionNums) {
        this.maxConnectionNum = maxConnectionNums;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = (Channel) msg;
        long conn = numConnections.incrementAndGet();
        if (conn > 0 && conn <= maxConnectionNum) {
            this.childChannels.add(channel);
            channel.closeFuture().addListener(future -> {
                childChannels.remove(channel);
                numConnections.decrementAndGet();
            });
            super.channelRead(ctx, msg);
        } else {
            numConnections.decrementAndGet();
            // Set linger option to 0 so that the server doesn't get too many TIME_WAIT states.
            channel.config().setOption(ChannelOption.SO_LINGER, 0);
            channel.unsafe().closeForcibly();// 关闭 channel
            numDroppedConnections.increment();
            if (loggingScheduled.compareAndSet(false, true)) {
                ctx.executor().schedule(this::writeNumDroppedConnectionsLog, 1, TimeUnit.SECONDS);
            }
        }
    }

    private void writeNumDroppedConnectionsLog() {
        loggingScheduled.set(false);
        final long dropped = numDroppedConnections.sumThenReset();
        if (dropped > 0) {
            log.warn("Dropped {} connection(s) to limit the number of open connections to {}",
                    dropped, maxConnectionNum);
        }
    }

    public int getMaxConnectionNum() {
        return maxConnectionNum;
    }

    public AtomicLong getNumConnections() {
        return numConnections;
    }

    public LongAdder getNumDroppedConnections() {
        return numDroppedConnections;
    }

    public Set<Channel> getChildChannels() {
        return childChannels;
    }
}