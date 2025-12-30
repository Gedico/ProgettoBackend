package ProgettoINSW.backend.service;

//import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface InserzioneService {

    InserzioneResponse creaInserzione(InserzioneRequest request, MultipartFile[] immagini, String token) throws IOException;


    InserzioneResponse getInserzioneById(Long id);


    List<InserzioneResponse> getAllInserzioni();


    List<InserzioneCardResponse> getInserzioniRecenti();

    List<InserzioneCardResponse> getInserzioniPerAgente(String token);
}
