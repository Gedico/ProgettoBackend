package ProgettoINSW.backend.dto.proposta;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ContropropostaRequest {
    @NotNull(message = "Il nuovo prezzo è obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo deve essere positivo")
    private BigDecimal nuovoPrezzo;

    private String note;
}
