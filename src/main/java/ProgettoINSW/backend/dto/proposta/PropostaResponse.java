package ProgettoINSW.backend.dto.proposta;

import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.model.enums.TipoProponente;
import ProgettoINSW.backend.model.enums.TipoProposta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class PropostaResponse {
    private Long idProposta;

    // Inserzione
    private Long idInserzione;
    private String titoloInserzione;
    private BigDecimal prezzoInserzione;

    // Importo e stato
    private BigDecimal importo;
    private StatoProposta stato;
    private OffsetDateTime dataCreazione;

    // Proponente
    private TipoProponente proponente;
    private Long idUtente;
    private String nomeCliente;  //solo manuale
    private String contattoCliente;
    private Long idAgente;
    private TipoProposta tipo;

    // Controproposta
    private Long idPropostaPrecedente;  // null se è la prima
    private String messaggio;           // eventuale commento / feedback

}
