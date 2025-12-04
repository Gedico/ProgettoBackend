package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import org.springframework.stereotype.Service;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface InserzioneService {

    InserzioneResponse creaInserzione(InserzioneRequest request, MultipartFile[] immagini, String token) throws IOException;

    DatiInserzioneRequest modificaInserzione(Long idInserzione, DatiInserzioneRequest request, String token);

    List<InserzioneResponse> ricercaInserzioni(DatiInserzioneFiltriRequest filtri);

    InserzioneResponse getInserzioneById(Long id);

    void eliminaInserzione(Long id, String token);

    List<InserzioneResponse> getAllInserzioni();

    void cambiaStato(Long id, String bearer, String nuovoStato);

    List<InserzioneCardResponse> getInserzioniRecenti();
}
