package com.scuop.imgservice.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

// 图片来源,便于分类
@Data
@ConfigurationProperties(prefix = "pic")
public class PicSource {

    private List<String> picSource;

    public int existPicSource(String source) {
        return picSource.indexOf(source);
    }
}
