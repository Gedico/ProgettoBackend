package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.datiInserzione.ModificaDatiInserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import org.springframework.stereotype.Service;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import java.util.List;

@Service
public interface InserzioneService {

    InserzioneResponse creaInserzione(InserzioneRequest request, String token);

    ModificaDatiInserzioneRequest modificaInserzione(Long idInserzione, ModificaDatiInserzioneRequest request, String token);

    List<InserzioneResponse> ricercaInserzioni(DatiInserzioneFiltriRequest filtri);

    InserzioneResponse getInserzioneById(Long id);

    void eliminaInserzione(Long id, String token);



}
