package com.logistics.utils;

import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 */
@Component
public class SnowflakeIdGenerator {
    
    // 起始时间戳：2023-01-01 00:00:00
    private final long START_TIMESTAMP = 1672502400000L;
    
    // 机器ID所占位数
    private final long WORKER_ID_BITS = 5L;
    
    // 数据中心ID所占位数
    private final long DATACENTER_ID_BITS = 5L;
    
    // 序列号所占位数
    private final long SEQUENCE_BITS = 12L;
    
    // 机器ID最大值
    private final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    
    // 数据中心ID最大值
    private final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    
    // 序列号最大值
    private final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // 机器ID向左移12位
    private final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    
    // 数据中心ID向左移17位
    private final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    // 时间戳向左移22位
    private final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    
    // 机器ID
    private long workerId = 1;
    
    // 数据中心ID
    private long datacenterId = 1;
    
    // 序列号
    private long sequence = 0L;
    
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;
    
    public SnowflakeIdGenerator() {
    }
    
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    /**
     * 获取下一个ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        
        // 如果当前时间小于上一次生成ID的时间戳，说明系统时钟回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }
        
        // 如果是同一毫秒内生成的，则进行毫秒内序列号递增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 毫秒内序列号溢出，阻塞到下一毫秒
            if (sequence == 0) {
                timestamp = getNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，重置序列号
            sequence = 0L;
        }
        
        // 更新上次生成ID的时间戳
        lastTimestamp = timestamp;
        
        // 组合生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 阻塞到下一毫秒，获得新的时间戳
     */
    private long getNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
} 