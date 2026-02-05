package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Table(name = "posizione")
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posizione")
    private Long idPosizione;

    @Column(name = "latitudine", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitudine;

    @Column(name = "longitudine", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitudine;

    @Column(nullable = false, length = 100)
    private String comune;

    @Column(nullable = false, length = 255)
    private String indirizzo;

    public Posizione() {
    }

    public Posizione(BigDecimal latitudine, BigDecimal longitudine, String comune, String indirizzo) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.comune = comune;
        this.indirizzo = indirizzo;
    }

}

