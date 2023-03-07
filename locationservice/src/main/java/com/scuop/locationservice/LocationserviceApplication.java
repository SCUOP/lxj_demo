package com.scuop.locationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.scuop.locationservice.config.FeginRequestInterceptor;
import com.scuop.routeservicefeignapi.client.RouteClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
// 过时
@EnableFeignClients(clients = RouteClient.class, defaultConfiguration = FeginRequestInterceptor.class)
@OpenAPIDefinition
public class LocationserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationserviceApplication.class, args);
	}

}
