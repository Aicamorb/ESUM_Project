package com.esum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Controller;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Controller
public class EsumApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsumApplication.class, args);
	}

}
