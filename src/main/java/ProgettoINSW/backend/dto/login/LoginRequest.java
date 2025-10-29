package ProgettoINSW.backend.dto.login;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LoginRequest {
    private String mail;
    private String password;

}
