package com.ocean;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication()
@EnableWebMvc
@EnableTransactionManagement
@MapperScan("com.ocean.dao")
public class OaWorkflowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaWorkflowServiceApplication.class, args);
    }

}
