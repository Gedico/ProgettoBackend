package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "utente")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
