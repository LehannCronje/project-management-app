package com.example.demo.Service;

import com.example.demo.Entity.MpxFile;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    public int uploadFile(MultipartFile file, String username);

    public void deleteFile(MpxFile file);

}