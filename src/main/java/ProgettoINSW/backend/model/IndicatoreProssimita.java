package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "indicatore_prossimita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicatoreProssimita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIndicatore;

    @OneToOne
    @JoinColumn(name = "id_inserzione", nullable = false, unique = true)
    private Inserzione inserzione;

    @Column(nullable = false)
    private boolean vicinoScuola;

    @Column(nullable = false)
    private boolean vicinoParco;

    @Column(nullable = false)
    private boolean vicinoMezziPubblici;
}

