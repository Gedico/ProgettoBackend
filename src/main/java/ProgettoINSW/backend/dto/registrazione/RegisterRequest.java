package ProgettoINSW.backend.dto.registrazione;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @Email
    @NotBlank
    private String mail;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String numero;

    @NotBlank
    private String approfondimento;

    // Non arriva dal frontend: lo setti tu nel service
    private String messaggio;

}