package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import ProgettoINSW.backend.dto.inserzionesearch.InserzioneSearchResponse;

import java.util.List;

public interface InserzioneSearchService {

    List<InserzioneSearchResponse> ricercaInserzioni(InserzioneSearchRequest request);

}
