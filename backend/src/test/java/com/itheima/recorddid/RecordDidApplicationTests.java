package com.itheima.recorddid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
// 扫描mapper文件夹，固定写法
@MapperScan("com.itheima.recorddid.mapper")
public class RecordDidApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(RecordDidApplication.class, args);
    }
}