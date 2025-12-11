package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.repository.FotoRepository;
import ProgettoINSW.backend.service.FotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FotoServiceImpl implements FotoService {

    private final CloudStorageServiceImpl cloudStorageService;
    private final FotoRepository fotoRepository;

    private static final int MAX_IMAGE_COUNT = 10;
    private static final long MAX_IMAGE_SIZE_BYTES = 5_000_000;

    @Override
    public List<Foto> processImages(MultipartFile[] files, Inserzione inserzione) throws IOException {

        if (files == null) {
            files = new MultipartFile[0];
        }

        if (files == null || files.length == 0) {
            return List.of(); // nessuna foto → nessun problema
        }

        if (files.length > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException(
                    "Puoi caricare massimo " + MAX_IMAGE_COUNT + " immagini."
            );
        }

        List<String> uploadedUrls = new ArrayList<>();
        List<Foto> fotoList = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                validateImage(file);

                String url = cloudStorageService.uploadFile(file);
                uploadedUrls.add(url);

                Foto f = new Foto();
                f.setUrlFoto(url);
                f.setInserzione(inserzione);
                fotoList.add(f);
            }

            fotoRepository.saveAll(fotoList);
            return fotoList;

        } catch (Exception e) {

            // rollback cloud → elimina ciò che è stato caricato prima dell'errore
            for (String url : uploadedUrls) {
                try {
                    cloudStorageService.deleteFile(url);
                } catch (Exception ignored) {}
            }

            throw e;
        }
    }

    @Override
    public void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Caricata un'immagine vuota.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Un'immagine supera il limite massimo di 5MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Formato immagine non riconosciuto.");
        }

        contentType = contentType.toLowerCase();

        if (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp")) {

            throw new IllegalArgumentException("Formato non supportato. Ammessi: JPG, PNG, WEBP.");
        }
    }
}

