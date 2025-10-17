package ProgettoINSW.backend.model;

import jakarta.persistence.*;
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
    private Date data_di_iscrizione;



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

    public Date getData_di_iscrizione() {
        return data_di_iscrizione;
    }

    public void setData_di_iscrizione(Date data_di_iscrizione) {
        this.data_di_iscrizione = data_di_iscrizione;
    }


    //Costruttore


    public Utente() {
    }

    public Utente(Long idUtente, Account account, String indirizzo, Date data_di_iscrizione) {
        this.idUtente = idUtente;
        this.account = account;
        this.indirizzo = indirizzo;
        this.data_di_iscrizione = data_di_iscrizione;
    }
}
