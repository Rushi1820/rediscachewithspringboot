package com.example.redisproject.redisproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisprojectApplication.class, args);
	}

}
