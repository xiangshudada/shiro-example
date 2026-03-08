package org.spring6.cache;


import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */
@Component
public class RedisCache<K, V> implements Cache<K, V> {

    private final String CACHE_PREFIX = "cache:";

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public V get(K k) throws CacheException {
        System.out.println("从Redis查询授权信息");
        V v = (V) redisTemplate.opsForValue().get(CACHE_PREFIX + k);
        if (v != null) {
            redisTemplate.expire(CACHE_PREFIX + k, 15, TimeUnit.MINUTES);
        }
        return v;
    }

    /**
     * 存放缓存信息
     *
     * @param k
     * @param v
     * @return
     * @throws CacheException
     */
    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForValue().set(CACHE_PREFIX + k, v, 15, TimeUnit.MINUTES);
        return v;
    }

    /**
     * 清空当前缓存
     *
     * @param k
     * @return
     * @throws CacheException
     */
    @Override
    public V remove(K k) throws CacheException {
        V v = (V) redisTemplate.opsForValue().get(CACHE_PREFIX + k);
        if (v != null) {
            redisTemplate.delete(CACHE_PREFIX + k);
        }
        return v;
    }

    /**
     * 清空全部的授权缓存
     *
     * @throws CacheException
     */
    @Override
    public void clear() throws CacheException {
        Set keys = redisTemplate.keys(CACHE_PREFIX + "*");
        redisTemplate.delete(keys);
    }

    /**
     * 查看有多个权限缓存信息
     *
     * @return
     */
    @Override
    public int size() {
        Set keys = redisTemplate.keys(CACHE_PREFIX + "*");
        return keys.size();
    }

    /**
     * 获取全部缓存信息的key
     *
     * @return
     */
    @Override
    public Set<K> keys() {
        Set keys = redisTemplate.keys(CACHE_PREFIX + "*");
        return keys;
    }

    /**
     * 获取全部缓存信息的value
     *
     * @return
     */
    @Override
    public Collection<V> values() {
        Set values = new HashSet();
        Set keys = redisTemplate.keys(CACHE_PREFIX + "*");
        for (Object key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            values.add(value);
        }
        return values;
    }
}
