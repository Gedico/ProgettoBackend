package ProgettoINSW.backend.dto.inserzione;

import ProgettoINSW.backend.model.Inserzione;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InserzioneResponse {

    private Inserzione inserzione;
    private String messaggio;

}
