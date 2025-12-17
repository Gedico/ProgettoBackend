package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.model.IndicatoreProssimita;
import ProgettoINSW.backend.model.Inserzione;

public interface IndicatoreProssimitaService {

    IndicatoreProssimita generaIndicatoriPerInserzione(InserzioneRequest inserzioneRequest);
}

