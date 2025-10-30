package ProgettoINSW.backend.dto.immobile;

import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.model.enums.Categoria;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ImmobileRequest {

private String titolo;
private String descrizione;
private BigDecimal prezzo;
private Integer dimensioni;
private Integer numero_stanze;
private Integer piano;
private Boolean ascensore;
private String classe_energetica;
private Categoria categoria;

}
