package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_account;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 20)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 20)
    private String cognome;

    @NotBlank(message = "La mail è obbligatoria")
    @Email(message = "La mail non è valida")
    @Column(unique = true)
    private String mail;

    @NotBlank(message = "La password è obbligatoria")
    private String password;

    @Size(max = 10)
    @Column(unique = true)
    private String numero;

    @NotBlank(message = "Il ruolo è obbligatorio")
    @Pattern(regexp = "ADMIN|AGENTE|UTENTE", message = "Ruolo non valido")
    private String ruolo;

}
