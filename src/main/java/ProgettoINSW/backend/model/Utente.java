package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "utente")
public class Utente {

    @Id
    @Column(name = "id_utente")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_account", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_utente_account"))
    private Account account;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name="data_registrazione", nullable = false)
    private LocalDateTime dataIscrizione;


    //Costruttore

    public Utente() {
    }

    public Utente(Long idUtente, Account account, String indirizzo, LocalDateTime data_di_iscrizione) {
        this.idUtente = idUtente;
        this.account = account;
        this.indirizzo = indirizzo;
        this.dataIscrizione = data_di_iscrizione;

    }
}
