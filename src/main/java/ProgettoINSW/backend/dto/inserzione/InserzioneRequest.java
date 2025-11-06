package ProgettoINSW.backend.dto.inserzione;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InserzioneRequest {

    private DatiInserzioneRequest datiInserzioneRequest;
    private PosizioneRequest posizione;
    private List<FotoRequest> foto;

}
