package ProgettoINSW.backend.dto.login;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String messaggio;
    private String ruolo;
    private String token;

    public LoginResponse() {}
    public LoginResponse(String messaggio, String ruolo, String token) {
        this.messaggio = messaggio;
        this.ruolo = ruolo;
        this.token = token;
    }

}
