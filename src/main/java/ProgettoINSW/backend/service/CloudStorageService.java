package ProgettoINSW.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudStorageService {


    String uploadFile(MultipartFile file) throws IOException;

    String getPublicUrl(String filename);
}

