package ProgettoINSW.backend.dto.proposta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropostaManualeRequest {

    @NotNull
    @Positive
    private BigDecimal prezzoProposta;

    @NotBlank
    private String nomeCliente;

    @NotBlank
    private String contattoCliente;

    private String note;

    @NotNull
    private LocalDate dataOfferta;
}
