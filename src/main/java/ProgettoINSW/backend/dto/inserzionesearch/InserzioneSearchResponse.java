package ProgettoINSW.backend.dto.inserzionesearch;

import ProgettoINSW.backend.model.enums.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InserzioneSearchResponse{

    private Long idInserzione;
    private String titolo;
    private String descrizione;
    private String comune;
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private BigDecimal prezzo;
    private int dimensione;
    private Integer stanze;
    private Integer piano;
    private Boolean ascensore;
    private String classeEnergetica;
    private List<String> fotoUrls;
    private Categoria tipo;
}

