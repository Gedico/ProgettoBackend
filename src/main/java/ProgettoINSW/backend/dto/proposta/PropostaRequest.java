package ProgettoINSW.backend.dto.proposta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PropostaRequest {

    @NotNull(message = "L'id dell'inserzione è obbligatorio")
    private Long idInserzione;

    @NotNull(message = "Il prezzo proposto è obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo deve essere positivo")
    private BigDecimal prezzoProposta;

    private String note;

}
