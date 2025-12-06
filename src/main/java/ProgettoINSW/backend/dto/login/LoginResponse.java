package ProgettoINSW.backend.dto.login;


import ProgettoINSW.backend.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String messaggio;
    private Role ruolo;
    private String token;
    private boolean success;

}
