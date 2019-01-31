package com.geotop.geotopproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeotopProjectApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GeotopProjectApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GeotopProjectApplication.class, args);
	}
}
