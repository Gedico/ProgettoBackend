package ProgettoINSW.backend.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "Formato email non valido")
    @NotBlank(message = "La mail è obbligatoria")
    private String mail;

    @NotBlank(message = "La password è obbligatoria")
    private String password;
}

