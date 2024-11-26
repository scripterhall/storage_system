package com.tekup.storage_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class StorageSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorageSystemApplication.class, args);
	}

}
