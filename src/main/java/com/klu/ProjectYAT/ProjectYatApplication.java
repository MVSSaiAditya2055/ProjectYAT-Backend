package com.klu.ProjectYAT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@RestController
@EnableAsync
public class ProjectYatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectYatApplication.class, args);
	}

	@GetMapping("/")
	public String healthCheck() {
		return "Backend is running!";
	}

}
