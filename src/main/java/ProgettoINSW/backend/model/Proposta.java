package ProgettoINSW.backend.model;
import ProgettoINSW.backend.model.enums.StatoProposta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "proposta")
@Getter
@Setter
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proposta")
    private Long idProposta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_inserzione", nullable = false)
    private Inserzione inserzione;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Utente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_agente", nullable = false)
    private Agente agente;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "prezzo_proposta", nullable = false)
    private BigDecimal prezzoProposta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_proposta", length = 20, nullable = false)
    private StatoProposta stato;

    @NotNull
    @Column(name = "data_proposta", nullable = false)
    private OffsetDateTime dataProposta;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;


    public Proposta() {
    }


    @PrePersist
    private void prePersist() {
        if (stato == null) {
            stato = StatoProposta.IN_ATTESA;
        }
        if (dataProposta == null) {
            dataProposta = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Proposta{" +
                "idProposta=" + idProposta +
                ", prezzoProposta=" + prezzoProposta +
                ", stato=" + stato +
                ", dataProposta=" + dataProposta +
                '}';
    }
}
