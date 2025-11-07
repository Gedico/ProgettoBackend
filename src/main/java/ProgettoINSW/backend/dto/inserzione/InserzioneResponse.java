package ProgettoINSW.backend.dto.inserzione;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneResponse;
import ProgettoINSW.backend.dto.foto.FotoResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneResponse;
import ProgettoINSW.backend.model.Inserzione;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InserzioneResponse {
    private Long id;
    private DatiInserzioneResponse dati;
    private PosizioneResponse posizione;
    private List<FotoResponse> foto;
    private String messaggio;
}

