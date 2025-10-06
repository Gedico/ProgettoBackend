package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "account")

public class account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccount;

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


    // Costruttore vuoto
    public account() {}

    // Getter e Setter
    public Long getIdAccount() { return idAccount; }
    public void setIdAccount(Long idAccount) { this.idAccount = idAccount; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

}
