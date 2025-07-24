package com.sun.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.model.GridFSFile;

import com.sun.api.controller.user.FileUploaderControllerApi;
import com.sun.exception.GraceException;
import com.sun.files.config.MinioClientConfig;
import com.sun.files.service.UploaderService;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.bo.NewAdminBO;
import com.sun.utils.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private MinioClientConfig minioConfig;

    @Autowired
    private GridFSBucket gridFSBucket;


    @Override
    public GraceJSONResult uploadFace(String userId, MultipartFile file) {
        String suffix = "";
        if (file != null) {
            // 获得文件上传的名称
            String fileName = file.getOriginalFilename();
            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀符合我们预定义规范
                if (!suffix.equalsIgnoreCase("jpg")) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                // 执行上传
                uploaderService.uploadMinio(file,userId + "." + suffix);

            }else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        System.out.println(minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userId + "." + suffix);
        return GraceJSONResult.ok(minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userId + "." + suffix);
    }

    @Override
    public GraceJSONResult uploadToGridFS(NewAdminBO newAdminBO) {
        // 获得图片的base64字符串
        String img64 = newAdminBO.getImg64();
        // 将img64转换为byte数组
        byte[] bytes = Base64.getDecoder().decode(img64.trim());
        // 转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // 上传到gridfs中
        ObjectId fileId= gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", inputStream);
        // 获得文件在gridfs主键
        String fileIdStr = fileId.toString();
        return GraceJSONResult.ok(fileIdStr);
    }

    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 判断参数不为空
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 从gridfs中读取
        File adminFace = readGridFSByFaceId(faceId);

        //把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(response,adminFace);

    }

    @Override
    public GraceJSONResult readFace64InGridFS(String faceId,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        // 0.获得gridfs中的人脸文件
        File myFace = readGridFSByFaceId(faceId);
        // 1.转换人脸为base64
        String base64 = FileUtils.fileToBase64(myFace);
        System.out.println("okok");
        return GraceJSONResult.ok(base64);
    }

    private File readGridFSByFaceId(String faceId) throws IOException {
        GridFSFindIterable gridFSFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile gridFS = gridFSFiles.first();

        if (gridFS == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        String fileName = gridFS.getFilename();
        System.out.println(fileName);

        // 获取文件流，保存文件到本地或者服务器的临时目录
        File fileTemp = new File("/Users/horizon/Desktop/project/sun-dev/temp_face");

        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }
        File myFile = new File("/Users/horizon/Desktop/project/sun-dev/temp_face/" + fileName);

        // 创建文件的输出流
        OutputStream os = new FileOutputStream(myFile);

        // 下载到本地
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);

        return myFile;
    }

    @Override
    public GraceJSONResult uploadSomeFiles(String userId, MultipartFile[] files) {

        // 声明一个list，用于存放多个图片的地址路径，返回到前端
        List<String> imageUrlList = new ArrayList<>();
        String timeStamp = System.currentTimeMillis() + "";
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String suffix = "";
                if (file != null) {
                    // 获得文件上传的名称
                    String fileName = file.getOriginalFilename();
                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        String fileNameArr[] = fileName.split("\\.");
                        // 获得后缀
                        suffix = fileNameArr[fileNameArr.length - 1];
                        // 判断后缀符合我们预定义规范
                        if (!suffix.equalsIgnoreCase("jpg") &&
                                !suffix.equalsIgnoreCase("png") &&
                                !suffix.equalsIgnoreCase("jpeg")) {
                            continue;
                        }
                        // 执行上传
                        uploaderService.uploadMinio(file,userId + ":article" + timeStamp + "." + suffix);

                    }else {
                        continue;
                    }
                }else {
                    continue;
                }
                imageUrlList.add(minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userId + ":article" + timeStamp + "." + suffix);
//                System.out.println(minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userId + "." + suffix);
//                return GraceJSONResult.ok(minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + userId + "." + suffix);
            }
        }

        return GraceJSONResult.ok(imageUrlList);
    }
}

