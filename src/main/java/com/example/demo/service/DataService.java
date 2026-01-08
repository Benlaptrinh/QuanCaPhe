package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface DataService {
    /**
     * Create a backup file on the server and return it.
     * The controller will stream it to the client and delete the file afterwards.
     */
    File createBackupFile();

    /**
     * Restore database from an uploaded SQL file.
     */
    void restoreFromFile(MultipartFile file);
}


