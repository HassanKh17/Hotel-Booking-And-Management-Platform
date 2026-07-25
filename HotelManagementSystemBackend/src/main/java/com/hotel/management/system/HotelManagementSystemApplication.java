package com.hotel.management.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HotelManagementSystemApplication {

	public static void main(String[] args) {
        SpringApplication.run(HotelManagementSystemApplication.class, args);
	}
}
