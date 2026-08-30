package com.itheima.recorddid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@MapperScan("com.itheima.recorddid.mapper") // 扫描mapper包
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class RecordDidApplication {

    public static void main(String[] args) {

        SpringApplication.run(RecordDidApplication.class, args);
    }

}
