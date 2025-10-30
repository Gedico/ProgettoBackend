package ProgettoINSW.backend.model;

import ProgettoINSW.backend.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
@Table(name = "account")
public class Account {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_account")
    private Long id;

    @Setter
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 20)
    @Column(name = "nome")
    private String nome;

    @Setter
    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 20)
    @Column(name = "cognome")
    private String cognome;

    @Setter
    @NotBlank(message = "La mail è obbligatoria")
    @Email(message = "La mail non è valida")
    @Column(name = "mail",unique = true)
    private String mail;

    @Setter
    @NotBlank(message = "La password è obbligatoria")
    @Column(name = "password")
    private String password;

    @Setter
    @Size(max = 10)
    @Column(name = "numero" ,unique = true)
    private String numero;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo", nullable = false, length = 10)
    private Role ruolo;

    public Account() {
    }

    public Account(Long id, String nome, String cognome, String mail, String password, String numero, Role ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.numero = numero;
        this.ruolo = ruolo;
    }
}
