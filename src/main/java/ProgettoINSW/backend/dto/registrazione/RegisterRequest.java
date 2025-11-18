package ProgettoINSW.backend.dto.registrazione;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    private String nome;
    private String cognome;
    private String mail;
    private String password;
    private String numero;
    private String approfondimento;
    private String messaggio;

}