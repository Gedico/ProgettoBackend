package ProgettoINSW.backend.dto.profilo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfiloRequest {
    private String nome;
    private String cognome;
    private String numero;
    private String indirizzo;
    private String password;

}
