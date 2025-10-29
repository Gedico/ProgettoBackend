package ProgettoINSW.backend.model;

import ProgettoINSW.backend.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "account")
public class Account {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_account")
    private Long id;

    @Setter
    @Getter
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 20)
    private String nome;

    @Getter
    @Setter
    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 20)
    private String cognome;

    @Setter
    @Getter
    @NotBlank(message = "La mail è obbligatoria")
    @Email(message = "La mail non è valida")
    @Column(unique = true)
    private String mail;

    @Setter
    @Getter
    @NotBlank(message = "La password è obbligatoria")
    private String password;

    @Setter
    @Getter
    @Size(max = 10)
    @Column(unique = true)
    private String numero;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role ruolo;

    @OneToOne(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Utente utente;



    //Getter e Setter

    // Costruttori


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
