package ProgettoINSW.backend.dto.inserzione;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.immobile.ImmobileRequest;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InserzioneRequest {

    private ImmobileRequest immobile;
    private PosizioneRequest posizione;
    private List<FotoRequest> foto;


}
