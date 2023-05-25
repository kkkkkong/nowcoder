package com.coder;

import com.coder.community.NowcoderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testString() {
        String testKey = "test:count";
        redisTemplate.opsForValue().set(testKey, 1);
        System.out.println(redisTemplate.opsForValue().get(testKey));
    }

    @Test
    public void testHash() {
        String testKey="test:user";
        redisTemplate.opsForHash().put(testKey,"id",1);
        redisTemplate.opsForHash().put(testKey, "zhangsan", 1);
        System.out.println(redisTemplate.opsForHash().get(testKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(testKey, "zhangsan"));


    }

    @Test
    public void testList() {
        String testKey="test:ids";
        redisTemplate.opsForList().leftPush(testKey, 101);
        redisTemplate.opsForList().leftPush(testKey, 102);
        System.out.println(redisTemplate.opsForList().size(testKey));

    }

}
