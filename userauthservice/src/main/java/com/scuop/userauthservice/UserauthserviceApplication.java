package com.scuop.userauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.scuop.imgservicefeignapi.client.ImgClient;
import com.scuop.routeservicefeignapi.client.RouteClient;
import com.scuop.userauthservice.util.ValidationRule;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * TODO: 该服务既是用户安全信息的服务，也是SSO认证中心（会不会有点耦合，后续考虑拆分？）
 * TODO: 生产环境记得加入caffeine缓存
 * TODO: 增删改查SQL语句后续优化
 */
@SpringBootApplication
@EnableConfigurationProperties(ValidationRule.class)
@EnableTransactionManagement
@EnableFeignClients(clients = { RouteClient.class,
		ImgClient.class })
@OpenAPIDefinition
@EnableAsync
public class UserauthserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserauthserviceApplication.class, args);
	}

}
