package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.immobile.ModificaImmobileRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ProgettoINSW.backend.dto.immobile.ImmobileFiltriRequest;
import java.util.List;

@Service
public interface ImmobileService {

    InserzioneResponse creaInserzione(InserzioneRequest request, String token);

    ModificaImmobileRequest modificaInserzione(Long idImmobile, ModificaImmobileRequest request, String token);

    List<InserzioneResponse> ricercaImmobili(ImmobileFiltriRequest filtri);

    InserzioneResponse getInserzioneById(Long id);

    void eliminaInserzione(Long id, String token);

    void caricaFotoImmobile(Long id, String token,List<FotoRequest> nuoveFoto);
}
