package com.ej.restaurant.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean set(final String key, Object value){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        }catch (Exception e){
            log.error("set error: key={}, value={}", key, value, e);
        }

        return result;
    }

    public boolean set(final String key, Object value, int expire){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, expire, TimeUnit.MILLISECONDS);
            result = true;
        }catch (Exception e){
            log.error("set error: key={}, value={}", key, value, e);
        }

        return result;
    }

    public boolean put(final String hash, final String key, Object value){
        boolean result = false;

        try {
            HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
            operations.put(hash, key, value);
            result = true;
        }catch (Exception e){
            log.error("put hash error: hash {}, key {}, value {}", hash, key, value, e);
        }
        return result;
    }

    public boolean add(final String key, Object value){
        boolean result = false;

        try {
            ListOperations<Serializable, Object> operations = redisTemplate.opsForList();
            operations.rightPush(key, value);
            result = true;
        }catch (Exception e){
            log.error("List push error: key {}, value {}", key, value, e);
        }
        return result;
    }

    public boolean hasKey(final String key){
        return redisTemplate.hasKey(key);
    }

    public void removePattery(final String pattern){
        Set<Serializable> keys = redisTemplate.keys(pattern);

        if(keys.size() > 0){
            redisTemplate.delete(keys);
        }
    }

    public void delete(final String key){
        if(redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
        }
    }

    public Object get(final String key){
        if(!redisTemplate.hasKey(key)){
            return null;
        }

        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    public Object get(final String hash, final String key){
        if(!redisTemplate.hasKey(hash)){
            return null;
        }

        HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
        return operations.get(hash, key);
    }
}
