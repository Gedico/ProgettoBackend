package ProgettoINSW.backend.dto.registrazione;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequestUtente {
    //Getter e Setter
    private String nome;
    private String cognome;
    private String mail;
    private String password;
    private String numero;
    private String indirizzo;
    private String messaggio;

}

