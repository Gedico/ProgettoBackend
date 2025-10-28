package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Entity
@Table(name = "utente")
public class Utente {


    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    @Getter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_account", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_utente_account"))
    private Account account;


    @Getter
    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name="data_di_iscrizione", nullable = false)
    private LocalDateTime dataIscrizione;



    //Getter e Setter

    public LocalDateTime getData_di_iscrizione() {
        return dataIscrizione;
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
