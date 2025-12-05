package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传，上传的文件：{}", file);
//        /*
//        * 阿里云实现
//        * */
//        if (file.isEmpty()) {
//            return Result.error("上传失败，文件为空");
//        }
//
//        try {
//            //获取原始文件名
//            String originalFilename = file.getOriginalFilename();
//
//            //截取原始文件名
//            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            String newFileName = UUID.randomUUID() + extension;
//
//            //文件的请求路径
//            String filePath = aliOssUtil.upload(file.getBytes(), newFileName);
//            return Result.success(filePath);
//        } catch (IOException e) {
//            log.error("文件上传失败，原因：{}", e.getMessage());
//            return Result.error(MessageConstant.UPLOAD_FAILED);
//        }

        /*
        因为文件上传功能需要依赖阿里云OSS服务，所以这里先不实现具体的上传功能,直接选择上传到本地
        * */
        if (file.isEmpty()) {
            return Result.error("上传失败，文件为空");
        }
        String UploadDir = "D:/Project/java_code/sky-take-out/datapath/";
        File dir = new File(UploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + extension;
        String filePath = UploadDir + newFileName;
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            log.error("文件上传失败，原因：{}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        log.info("上传成功，文件保存路径：{}", filePath);
        String url = "http://localhost:8080/datapath/" + newFileName;
        return Result.success(url);
    }
}
