package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;

public interface DataService {
    



    File createBackupFile();

    


    void restoreFromFile(MultipartFile file);
}


