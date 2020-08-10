package com.jun.vxin.controller;

import com.jun.commons.response.Result;
import com.jun.vxin.domain.bo.UserBo;
import com.jun.vxin.domain.vo.UserVo;
import com.jun.vxin.service.OssService;
import com.jun.vxin.service.impl.UsersServiceImpl;
import com.jun.vxin.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@RestController
@RequestMapping("/image")
public class FileController {

    @Autowired
    private OssService ossService;

    @Autowired
    private UsersServiceImpl usersService;

    @PostMapping("/upload_user_head_image")
    public Result upload(@RequestBody UserBo userBo) throws Exception{

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBo.getFaceData();
        String userFacePath = "D:\\vxin\\" + userBo.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        // 上传文件到oss
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);

        String filePath = ossService.uploadImage(faceFile);

        UserVo data = usersService.updateUserInfo(userBo.getUserId(),userBo.getNickname(),filePath);

        return Result.ok("数据更新成功",data);
    }

}
