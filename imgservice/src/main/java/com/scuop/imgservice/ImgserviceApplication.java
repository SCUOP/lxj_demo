package com.scuop.imgservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.scuop.imgservice.util.PicSource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
@EnableConfigurationProperties(PicSource.class)
public class ImgserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImgserviceApplication.class, args);
	}

}
