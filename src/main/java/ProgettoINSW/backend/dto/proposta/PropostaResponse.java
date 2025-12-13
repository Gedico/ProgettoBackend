package ProgettoINSW.backend.dto.proposta;

import ProgettoINSW.backend.model.enums.StatoProposta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class PropostaResponse {
    private Long idProposta;
    private String titoloInserzione;
    private BigDecimal importo;
    private BigDecimal prezzoInserzione;
    private StatoProposta stato;
    private OffsetDateTime dataCreazione;
    private String messaggio; //nel caso feedback
}
