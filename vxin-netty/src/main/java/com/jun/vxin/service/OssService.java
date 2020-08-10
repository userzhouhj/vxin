package com.jun.vxin.service;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.jun.vxin.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Service
public class OssService {

    @Resource
    private OSS ossClient;
    @Autowired
    private OssProperties ossProperties;

    public String uploadImage(MultipartFile file){
        //文件名称路径拼接
        String filePath = new DateTime().toString("yyyy/MM/dd");

        String fileName = UUID.randomUUID().toString().replaceAll("-", "")+"."+
                file.getOriginalFilename();
        String fileUrl = filePath+"/"+fileName;

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            ossClient.putObject(ossProperties.getBucketName(), fileUrl, inputStream);

            return "https://"+ossProperties.getBucketName()+"."+ossProperties.getEndpoint()+"/" + fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {

        }

    }

}
