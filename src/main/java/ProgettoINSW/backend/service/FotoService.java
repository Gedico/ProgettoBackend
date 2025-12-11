package ProgettoINSW.backend.service;

import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FotoService {

    List<Foto> processImages(MultipartFile[] files, Inserzione inserzione) throws IOException;

    void validateImage(MultipartFile file);
}

