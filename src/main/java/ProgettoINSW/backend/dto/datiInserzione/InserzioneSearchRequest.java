package ProgettoINSW.backend.dto.datiInserzione;

import ProgettoINSW.backend.model.enums.Categoria;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InserzioneSearchRequest {

    @NotBlank(message = "Il comune è obbligatorio")
    private String comune;

    private BigDecimal prezzoMin;
    private BigDecimal prezzoMax;

    private Integer dimensioniMin;
    private Integer dimensioniMax;

    private Integer numeroStanze;

    private Integer pianoMin;
    private Integer pianoMax;

    private Boolean ascensore;

    private String classeEnergetica;

    private Categoria categoria; // enum Categoria (come String)

    private StatoInserzione stato;     // enum StatoInserzione (come String)
}

