package ProgettoINSW.backend.dto.registrazione;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @Email(message = "Email non valida")
    @NotBlank(message = "La mail è obbligatoria")
    private String mail;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    private String password;

    @NotBlank(message = "Il numero è obbligatorio")
    private String numero;

    // Campi opzionali
    private String indirizzo;
    private String approfondimento;
    private String messaggio;
}
