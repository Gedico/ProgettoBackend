package ProgettoINSW.backend.dto.registrazione;

import ProgettoINSW.backend.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private Long idAccount;
    private String nome;
    private String cognome;
    private String mail;
    private String numero;
    private Role ruolo;
    private String messaggio;
    private boolean success;

}
