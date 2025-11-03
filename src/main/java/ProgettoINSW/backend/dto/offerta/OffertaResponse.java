package ProgettoINSW.backend.dto.offerta;

import ProgettoINSW.backend.model.enums.StatoOfferta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class OffertaResponse {
    private Long idOfferta;
    private String titoloImmobile;
    private BigDecimal importo;
    private StatoOfferta stato;
    private OffsetDateTime dataCreazione;
    private String messaggio; //nel caso feedback
}
