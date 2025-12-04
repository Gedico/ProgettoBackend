package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.service.CloudStorageService;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {

    private final String bucketName = "dietiestate25"; // <-- usa il tuo bucket

    private final Storage storage;

    public CloudStorageServiceImpl() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {

        String objectName = "dietiestate25/Inserzioni/" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
    }

    @Override
    public String getPublicUrl(String filename) {
        return "https://storage.googleapis.com/" + bucketName + "/inserzioni/" + filename;
    }
}

