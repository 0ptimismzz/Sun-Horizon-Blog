package com.sun.api.controller.user;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.NewAdminBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "文件上传", description = "文件上传的Controller")
@RequestMapping("fs")
public interface FileUploaderControllerApi {

    /*
    api相当于领导
    其他的服务层都是实现
    运作：
    现在的所有接口都在此暴露，实现都在各自的微服务中
    本模块只写接口，不写实现
     */
    @Operation(summary = "上传用户头像", description = "上传用户头像", method = "POST")
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam String userId, MultipartFile file);

    /*
    文件上传到mongodb的gridfs中
     */
    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadToGridFS(@RequestBody NewAdminBO newAdminBO);

    /*
    从gridfs中读取图片内容
     */
    @GetMapping("/readInGridFS")
    public void readInGridFS(String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException;

    /*
    从gridFS中读取图片内容,并且返回base64
     */
    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFace64InGridFS(@RequestParam String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException;

    /**
     * 上传多个文件
     * @param userId
     * @param file
     * @return
     */
    @PostMapping("/uploadSomeFiles")
    public GraceJSONResult uploadSomeFiles(@RequestParam String userId, MultipartFile[] files);
}
