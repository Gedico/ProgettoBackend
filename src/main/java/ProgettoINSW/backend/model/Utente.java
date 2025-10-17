package ProgettoINSW.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "utente")
public class Utente {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    @OneToOne
    @JoinColumn(name = "id_account", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_utente_account"))
    private Account account;

   @Column(name = "indirizzo", length = 255)
    private String indirizzo;

    @Column(name="data_di_iscrizione", nullable = false)
    private LocalDateTime dataIscrizione;



    //Getter e Setter

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public LocalDateTime getData_di_iscrizione() {
        return dataIscrizione;
    }

    public void setDataIscrizione(LocalDateTime dataIscrizione) {
        this.dataIscrizione = dataIscrizione;
    }


    //Costruttore


    public Utente() {
    }

    public Utente(Long idUtente, Account account, String indirizzo, Date data_di_iscrizione) {
        this.idUtente = idUtente;
        this.account = account;
        this.indirizzo = indirizzo;
        this.dataIscrizione = dataIscrizione;
    }
}
