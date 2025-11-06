package ProgettoINSW.backend.dto.proposta;

import ProgettoINSW.backend.model.enums.StatoProposta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggiornaStatoPropostaRequest {
    private StatoProposta nuovoStato;
}
