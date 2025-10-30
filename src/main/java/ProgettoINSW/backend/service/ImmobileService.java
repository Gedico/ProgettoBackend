package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import org.springframework.stereotype.Service;

@Service
public interface ImmobileService {

    InserzioneResponse creaInserzione(InserzioneRequest request, String token);

}
