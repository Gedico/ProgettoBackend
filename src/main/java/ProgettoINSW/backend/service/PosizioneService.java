package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.model.Posizione;

public interface PosizioneService {
    Posizione creaPosizione(PosizioneRequest dto);
}

