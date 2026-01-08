package com.example.demo.service.impl;

import com.example.demo.service.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DataServiceImpl implements DataService {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:root}")
    private String dbUser;

    @Value("${spring.datasource.password:}")
    private String dbPass;

    private String parseDbName() {
        if (datasourceUrl == null) return "";
        // example: jdbc:mysql://host:port/dbname?params
        int idx = datasourceUrl.lastIndexOf('/');
        if (idx < 0) return datasourceUrl;
        String after = datasourceUrl.substring(idx + 1);
        int q = after.indexOf('?');
        if (q >= 0) return after.substring(0, q);
        return after;
    }

    private void execCommand(String[] command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            StringBuilder err = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    err.append(line).append('\n');
                }
            }
            int rc = process.waitFor();
            if (rc != 0) {
                throw new RuntimeException("Command failed (exit " + rc + "): " + err.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing command", e);
        }
    }

    @Override
    public File createBackupFile() {
        String dbName = parseDbName();
        try {
            Path tmp = Files.createTempFile("backup-", ".sql");
            String path = tmp.toAbsolutePath().toString();
            String cmd = String.format("mysqldump -u%s -p%s %s -r %s", dbUser, dbPass, dbName, path);
            String[] command = isWindows() ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
            execCommand(command);
            return tmp.toFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create backup file: " + e.getMessage(), e);
        }
    }

    @Override
    public void restoreFromFile(MultipartFile file) {
        String dbName = parseDbName();
        try {
            // save uploaded file to temp
            Path tmp = Files.createTempFile("restore-", ".sql");
            try (InputStream in = new BufferedInputStream(file.getInputStream());
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmp.toFile()))) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
            }

            String path = tmp.toAbsolutePath().toString();
            String cmd = String.format("mysql -u%s -p%s %s < %s", dbUser, dbPass, dbName, path);
            String[] command = isWindows() ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
            execCommand(command);
            // delete temp file
            try { Files.deleteIfExists(tmp); } catch (Exception ignored) {}
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore database", e);
        }
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && os.toLowerCase().contains("win");
    }
}


