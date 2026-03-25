package com.mes.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    // ==================== String ====================

    /** 设置缓存 */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置缓存并设置过期时间 */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 获取缓存 */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除缓存 */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /** 批量删除 */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /** 判断 key 是否存在 */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /** 设置过期时间 */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /** 获取过期时间 */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /** 递增 */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /** 递增指定值 */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /** 递减 */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // ==================== Hash ====================

    /** HashGet */
    public Object hGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /** 获取 hashKey 对应的所有键值 */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /** HashSet */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /** HashSet 并设置过期时间 */
    public void hSetAll(String key, Map<String, Object> map, long timeout, TimeUnit unit) {
        redisTemplate.opsForHash().putAll(key, map);
        expire(key, timeout, unit);
    }

    /** 向一张 hash 表中放入数据 */
    public void hSet(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /** 删除 hash 表中的值 */
    public Long hDelete(String key, Object... items) {
        return redisTemplate.opsForHash().delete(key, items);
    }

    /** 判断 hash 表中是否有该项的值 */
    public Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    // ==================== Set ====================

    /** 根据 key 获取 Set 中的所有值 */
    public Set<Object> sGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /** 根据值从一个 set 中查询是否存在 */
    public Boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /** 将数据放入 set 缓存 */
    public Long sSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /** 获取 set 缓存的长度 */
    public Long sGetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /** 移除值为 value 的 */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ==================== List ====================

    /** 获取 list 缓存的内容 */
    public List<Object> lGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /** 获取 list 缓存的长度 */
    public Long lGetSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /** 通过索引获取 list 中的值 */
    public Object lGetIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /** 将 list 放入缓存 */
    public Long lSet(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /** 将 list 放入缓存 */
    public Long lSetAll(String key, List<Object> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /** 移除 N 个值为 value 的 */
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
}
