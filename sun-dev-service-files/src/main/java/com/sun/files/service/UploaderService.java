package com.sun.files.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {

    public boolean uploadMinio(MultipartFile file, String objectName);

}
