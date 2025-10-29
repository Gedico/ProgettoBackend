package ProgettoINSW.backend.dto.registrazione;

import ProgettoINSW.backend.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {

    private Long idAccount;
    private String nome;
    private String cognome;
    private String mail;
    private String numero;
    private Role ruolo;
    private String messaggio;

    //Getter e Setter


}
