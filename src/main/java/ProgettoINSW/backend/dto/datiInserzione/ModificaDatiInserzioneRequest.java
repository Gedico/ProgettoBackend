package ProgettoINSW.backend.dto.datiInserzione;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ModificaDatiInserzioneRequest {

    private String titolo;
    private String descrizione;
    private BigDecimal prezzo;
    private Integer dimensioni;
    private Integer numeroStanze;
    private Integer piano;
    private Boolean ascensore;
    private String classeEnergetica;
    private String categoria;


    public ModificaDatiInserzioneRequest(String s, boolean b) {
    }
}
