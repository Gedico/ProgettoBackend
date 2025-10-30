package ProgettoINSW.backend.dto.inserzione;

import ProgettoINSW.backend.model.Immobile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InserzioneResponse {

    private Immobile immobile;
    private String messaggio;

}
