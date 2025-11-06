package ProgettoINSW.backend.model;
import ProgettoINSW.backend.model.enums.StatoProposta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
@Entity
@Table(name = "proposta")
public class Proposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proposta")
    private Long idProposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inserzione", nullable = false)
    private Inserzione inserzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Utente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente")
    private Agente agente;

    @Column(name = "prezzo_proposta",  nullable = false)
    @DecimalMin("0.0")
    private BigDecimal prezzoProposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_proposta", length = 20, nullable = false)
    private StatoProposta stato= StatoProposta.IN_ATTESA;


    @Column(name = "data_proposta", nullable = false)
    private OffsetDateTime dataProposta = OffsetDateTime.now();

    @Column(name = "note" , columnDefinition = "TEXT")
    private String note;


    //Costruttori

    public Proposta() {
    }
    

    @Override
    public String toString() {
        return "Proposta{" +
                "idProposta=" + idProposta +
                ", Idinserzione=" + (inserzione != null ? inserzione.getIdInserzione() : null) +
                ", clienteId=" + (cliente != null ? cliente.getIdUtente() : null) +
                ", agenteId=" + (agente != null ? agente.getIdAgente() : null) +
                ", prezzoproposta=" + prezzoProposta +
                ", statoproposta='" + stato + '\'' +
                ", dataproposta=" + dataProposta +
                ", note='" + note + '\'' +
                '}';
    }
}
