package ProgettoINSW.backend.model;

import ProgettoINSW.backend.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_account")
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role ruolo;

    @OneToOne(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Utente utente;



    //Getter e Setter

    public Long getId() {return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Role getRuolo() {
        return ruolo;
    }
    public void setRuolo(Role ruolo) {
        this.ruolo = ruolo;
    }

    // Costruttori


    public Account() {
    }

    public Account(Long id, String nome, String cognome, String email, String password, String numero, Role ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.mail = mail;
        this.password = password;
        this.numero = numero;
        this.ruolo = ruolo;
    }
}
