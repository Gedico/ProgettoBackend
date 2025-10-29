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

    @Column(nullable = false)
    private BigDecimal latitudine;

    @Column(nullable = false)
    private BigDecimal longitudine;

    @Column(length = 255)
    private String descrizione;


    //Getter e Setter


    //Costruttori


    public Posizione() {
    }


    public Posizione(Long idPosizione, BigDecimal latitudine, BigDecimal longitudine, String descrizione) {
        this.idPosizione = idPosizione;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.descrizione = descrizione;
    }
}

