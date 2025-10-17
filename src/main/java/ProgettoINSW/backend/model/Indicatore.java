package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "agente")
public class Indicatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agente")
    private Long idIndicatore;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoIndicatore tipo;

    @Column(name = "distanza", precision = 6, scale = 2)
    private Double distanza;

    @ManyToOne
    @JoinColumn(name = "id_inserzione", nullable = false,
            foreignKey = @ForeignKey(name = "fk_indicatore_inserzione"))
    private Immobile immobile;

    public enum TipoIndicatore {
        SCUOLA, PARCO, CENTRO, RISTORANTE, ALTRO
    }

    //Getter e Setter


    public Long getIdIndicatore() {
        return idIndicatore;
    }

    public void setIdIndicatore(Long idIndicatore) {
        this.idIndicatore = idIndicatore;
    }

    public TipoIndicatore getTipo() {
        return tipo;
    }

    public void setTipo(TipoIndicatore tipo) {
        this.tipo = tipo;
    }

    public Double getDistanza() {
        return distanza;
    }

    public void setDistanza(Double distanza) {
        this.distanza = distanza;
    }

    public Immobile getImmobile() {
        return immobile;
    }

    public void setImmobile(Immobile immobile) {
        this.immobile = immobile;
    }


    //Costruttori

    public Indicatore() {
    }

    public Indicatore(Long idIndicatore, TipoIndicatore tipo, Double distanza, Immobile immobile) {
        this.idIndicatore = idIndicatore;
        this.tipo = tipo;
        this.distanza = distanza;
        this.immobile = immobile;
    }
}
