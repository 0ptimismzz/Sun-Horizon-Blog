package com.sun.files.service.impl;

import com.sun.files.config.MinioClientConfig;
import com.sun.files.service.UploaderService;
import com.sun.utils.CookieUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    public MinioClient minioClient;

    @Autowired
    public MinioClientConfig minioClientConfig;

    @Override
    public boolean uploadMinio(MultipartFile file, String objectName) {

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioClientConfig.getBucketName())
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(file.getContentType()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }




//    @Service
//    public class MinioService {
//        @Autowired
//        private MinioClient minioClient;
//        @Value("${minio.bucketName}")
//        private String bucketName;
//        /**
//         * * 上传文件*
//         *
//         * @param file 要上传的文件
//         * @param objectName 存储在MinIO中的对象名称
//         *
//         * */
//        public void uploadFile(MultipartFile file, String objectName) {
//            try (InputStream inputStream = file.getInputStream()) {
//                minioClient.putObject(PutObjectArgs.builder()
//                                                    .bucket(bucketName)
//                        .object(objectName)
//                        .stream(inputStream, inputStream.available(), -1)
//                        .contentType(file.getContentType()).build());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }/*** 下载文件** @param objectName 要下载的对象名称* @return 文件的输入流*/
//        public InputStream downloadFile(String objectName) {try {return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());} catch (Exception e) {e.printStackTrace();return null;}}/*** 删除文件** @param objectName 要删除的对象名称*/public void deleteFile(String objectName) {try {minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());} catch (Exception e) {e.printStackTrace();}}/*** 获取文件的访问URL** @param objectName 要获取URL的对象名称* @return 文件的访问URL*/public String getFileUrl(String objectName) {try {return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).method(Method.GET).build());} catch (Exception e) {e.printStackTrace();return null;}}}

}
