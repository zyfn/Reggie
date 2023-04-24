package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j //lombok提供的日志功能，可以直接使用log
@SpringBootApplication
@ServletComponentScan //扫描拦截器
@EnableCaching //开启缓存
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
    }
}
