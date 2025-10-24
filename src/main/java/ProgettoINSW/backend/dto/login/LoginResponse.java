package ProgettoINSW.backend.dto.login;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String messaggio;
    private String ruolo;

    public LoginResponse() {}
    public LoginResponse(String messaggio, String ruolo) {
        this.messaggio = messaggio;
        this.ruolo = ruolo;
    }

}
