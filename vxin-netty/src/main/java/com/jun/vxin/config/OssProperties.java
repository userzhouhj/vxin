package com.jun.vxin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author ：userzhou
 * @date ：Created in 2020/4/8 9:37
 */
@ConfigurationProperties(prefix = "aliyun.oss.file")
@Data
public class OssProperties {


    private String endpoint;

    private String keyId;

    private String keySecret;

    private String bucketName;
}
