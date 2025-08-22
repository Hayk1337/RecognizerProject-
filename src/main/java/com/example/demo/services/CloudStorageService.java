package com.example.demo.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.demo.util.Constants.DOWNLOAD_BASE_PATH;
import static com.example.demo.util.GlobalConstants.*;

@Service
public class CloudStorageService {
    public void uploadAudio(String fileName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
        String filePath = DOWNLOAD_BASE_PATH + fileName;

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            storage.create(blobInfo, bytes);
            System.out.println("File uploaded to Google Cloud Storage successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
