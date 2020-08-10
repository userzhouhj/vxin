package com.jun.vxin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@EnableConfigurationProperties(OssProperties.class)
@Configuration
public class OssConfig {

    @Autowired
    private OssProperties ossProperties;

    @Bean("ossClient")
    public OSS client(){
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getKeyId(), ossProperties.getKeySecret());
        return ossClient;
    }
}
