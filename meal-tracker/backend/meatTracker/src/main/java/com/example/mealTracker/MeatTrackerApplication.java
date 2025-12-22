package com.example.mealTracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mealTracker.mapper")
public class MeatTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeatTrackerApplication.class, args);
    }

}
