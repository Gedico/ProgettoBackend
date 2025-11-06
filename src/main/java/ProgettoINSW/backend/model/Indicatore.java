package ProgettoINSW.backend.model;

import ProgettoINSW.backend.model.enums.Tipologia;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "indicatore_prox")
public class Indicatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicatore")
    private Long idIndicatore;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private Tipologia tipo;

    @Column(name = "distanza")
    private BigDecimal distanza;

    @ManyToOne
    @JoinColumn(name = "id_inserzione", nullable = false,
            foreignKey = @ForeignKey(name = "fk_indicatore_inserzione"))
    private Inserzione inserzione;

    public Indicatore() {
    }

    public Indicatore(Long idIndicatore, Tipologia tipo, BigDecimal distanza, Inserzione inserzione) {
        this.idIndicatore = idIndicatore;
        this.tipo = tipo;
        this.distanza = distanza;
        this.inserzione = inserzione;
    }
}
