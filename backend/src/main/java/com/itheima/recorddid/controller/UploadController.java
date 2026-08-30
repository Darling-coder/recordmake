package com.itheima.recorddid.controller;

import com.itheima.recorddid.common.Result;
import com.itheima.recorddid.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.prefix}")
    private String prefix;

    // 上传图片接口，需要登录token
    @PostMapping("/img")
    public Result<String> uploadImg(@RequestParam MultipartFile file) throws Exception {
        // 1. 判断文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }
        // 2. 限制只能图片
        String contentType = file.getContentType();
        if (!contentType.contains("image")) {
            throw new BusinessException(400, "仅支持jpg/png/jpeg图片");
        }
        // 3. 生成唯一文件名，避免覆盖
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        // 4. 文件夹不存在则创建
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 5. 保存文件到本地
        File targetFile = new File(uploadPath + fileName);
        file.transferTo(targetFile);

        // 6. 返回完整访问URL，存入record表cover字段
        String url = prefix + fileName;
        return Result.success(url);
    }
}