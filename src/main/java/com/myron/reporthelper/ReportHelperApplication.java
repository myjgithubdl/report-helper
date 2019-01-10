package com.myron.reporthelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan(basePackages = "com.myron.reporthelper")
public class ReportHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportHelperApplication.class, args);
    }

}

