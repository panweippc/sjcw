package com.sjcw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sjcw.mapper")
public class SjcwApplication {

    public static void main(String[] args) {
        SpringApplication.run(SjcwApplication.class, args);
    }
}
