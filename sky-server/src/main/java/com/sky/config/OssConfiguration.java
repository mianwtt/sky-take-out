package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* 配置类，用于创建AliOssUtil对象
* 配置类的作用就是，在Spring容器启动时，创建并管理一个对应对象（变成Spring的Bean），便于项目中任意地地方直接注入使用
* */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建AliOssUtil对象，AliOssProperties：{}", aliOssProperties);
        AliOssUtil aliOssUtil = new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
        log.info("AliOssUtil对象创建成功：{}", aliOssUtil);
        return aliOssUtil;
    }
}
