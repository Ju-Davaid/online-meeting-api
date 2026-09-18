package api.meeting.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
@RequiredArgsConstructor
public class RedisUtils<V> {
    private final RedisTemplate<String, V> redisTemplate;

    // ====================== String 字符串 ======================

    /**
     * 设置key-value，永不过期
     */
    public void set(String key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置key-value，带过期时间
     *
     * @param time 过期时长
     * @param unit 时间单位 TimeUnit.SECONDS
     */
    public void set(String key, V value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, time, unit);
    }

    /**
     * 获取值
     */
    public V get(String key) {
        return (V) redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增
     */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ====================== Hash 哈希 ======================
    public void hSet(String key, String hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void hMultiSet(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    public void hDel(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ====================== List 列表 ======================
    public void lPush(String key, V value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void rPush(String key, V value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public List<V> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    // ====================== Set 集合 ======================
    public Long sAdd(String key, V... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Set<V> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ====================== ZSet 有序集合 ======================
    public Boolean zAdd(String key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<V> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<V> zRevRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    // ====================== 通用操作 ======================

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除
     */
    public Long del(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long time, TimeUnit unit) {
        return redisTemplate.expire(key, time, unit);
    }

    /**
     * 获取剩余过期时间（单位秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ====================== 简单分布式锁（非可重入，生产推荐Redisson） ======================

    /**
     * 获取锁
     *
     * @param key        锁key
     * @param value      标识（建议UUID，释放锁校验）
     * @param expireTime 过期时间 秒
     */
    public Boolean tryLock(String key, V value, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 释放锁（必须校验value，防止误删别人锁）
     */
    public boolean unLock(String key, String value) {
        Object currentVal = get(key);
        if (value.equals(currentVal)) {
            return delete(key);
        }
        return false;
    }
}