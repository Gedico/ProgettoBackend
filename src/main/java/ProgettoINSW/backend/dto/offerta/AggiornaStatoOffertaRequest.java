package ProgettoINSW.backend.dto.offerta;

import ProgettoINSW.backend.model.enums.StatoOfferta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggiornaStatoOffertaRequest {
    private StatoOfferta nuovoStato;
}
