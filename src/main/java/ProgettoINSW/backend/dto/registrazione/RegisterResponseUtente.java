package ProgettoINSW.backend.dto.registrazione;

import ProgettoINSW.backend.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
public class RegisterResponseUtente {

    @Getter
    private Long idAccount;
    @Getter
    private String nome;
    @Getter
    private String cognome;
    @Getter
    private String mail;
    @Getter
    private String numero;
    @Getter
    private String indirizzo;
    @Getter
    private Role ruolo;
    private String messaggio;

    //Getter e Setter


}
