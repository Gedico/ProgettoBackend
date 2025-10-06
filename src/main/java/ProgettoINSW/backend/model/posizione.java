package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "posizione")

public class posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posizione")
    private Long idPosizione;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitudine;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitudine;

    @Column(length = 255)
    private String descrizione;

    // Costruttore vuoto (obbligatorio per JPA)
    public posizione() {
    }

    // Costruttore completo
    public posizione(BigDecimal latitudine, BigDecimal longitudine, String descrizione) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.descrizione = descrizione;
    }

    // Getter e Setter
    public Long getIdPosizione() {
        return idPosizione;
    }

    public void setIdPosizione(Long idPosizione) {
        this.idPosizione = idPosizione;
    }

    public BigDecimal getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(BigDecimal latitudine) {
        this.latitudine = latitudine;
    }

    public BigDecimal getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(BigDecimal longitudine) {
        this.longitudine = longitudine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    // toString()
    @Override
    public String toString() {
        return "Posizione{" +
                "idPosizione=" + idPosizione +
                ", latitudine=" + latitudine +
                ", longitudine=" + longitudine +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }

}

