package com.scuop.imgservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IMGConfig implements WebMvcConfigurer {

    // 注入配置文件中写好的图片保存路径
    @Value("${path.filepath}")
    private String filePath;

    @Value("${path.accesspath}")
    private String accessPath;

    // 自定义资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(accessPath + "**")
                .addResourceLocations("file:" + filePath);
    }
}
