package com.pinyougou.shop.controller;

import com.pinyougou.common.FastDFSClient;
import com.pinyougou.entity.Result;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by a2363196581 on 2018/3/11.
 */
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result upload(MultipartFile file){
        String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        try {
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:properties/fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), substring);
            System.out.println(path);
            return new Result(true, FILE_SERVER_URL+path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }

    }



}
