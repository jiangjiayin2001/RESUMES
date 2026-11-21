package com.resumestudio;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@MapperScan(basePackages = "com.resumestudio", annotationClass = Mapper.class)
@SpringBootApplication
public class ResumeStudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeStudioApplication.class, args);
    }
}
