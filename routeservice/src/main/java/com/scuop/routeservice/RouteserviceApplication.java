package com.scuop.routeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import com.scuop.imgservicefeignapi.client.ImgClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
@EnableAsync
@EnableFeignClients(clients = ImgClient.class)
public class RouteserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteserviceApplication.class, args);
	}

}
