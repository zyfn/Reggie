package com.itheima.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class App {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void Test(){
        redisTemplate.opsForValue().set("city","beijing");
    }


    List<List<Integer>> result = new ArrayList<>();
    List<Integer> list = new ArrayList<>();
    @Test
    public void MyTest(){
        for (int i = 0; i <result.size() ; i++) {
            System.out.println(result.get(i));
        }
    }

}
