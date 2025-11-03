package ProgettoINSW.backend.dto.immobile;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ModificaImmobileRequest {

    private String titolo;
    private String descrizione;
    private BigDecimal prezzo;
    private Integer dimensioni;
    private Integer numeroStanze;
    private Integer piano;
    private Boolean ascensore;
    private String classeEnergetica;
    private String categoria;


    public ModificaImmobileRequest(String immobileAggiornatoConSuccesso, boolean b) {
    }
}
