package ProgettoINSW.backend.dto.profilo;

import ProgettoINSW.backend.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfiloResponse {
    private String nome;
    private String cognome;
    private String numero;
    private String indirizzo;
    private String mail;
    private Role ruolo;
}
