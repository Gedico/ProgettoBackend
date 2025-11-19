package ProgettoINSW.backend.dto.inserzione;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InserzioneCardResponse {

    private Long idInserzione;

    private String titolo;
    private BigDecimal prezzo;
    private Integer dimensioni;
    private Integer numero_stanze;

    private String fotoPrincipale;  // solo la prima foto
}
