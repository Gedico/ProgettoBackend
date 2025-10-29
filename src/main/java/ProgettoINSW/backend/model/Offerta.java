package ProgettoINSW.backend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
@Entity
@Table(name = "offerta")
public class Offerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_offerta")
    private Long idOfferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobile", nullable = false)
    private Immobile immobile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Utente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente")
    private Agente agente;

    @Column(name = "prezzo_offerta",  nullable = false)
    @DecimalMin("0.0")
    private BigDecimal prezzoOfferta;

    @Column(name = "stato_offerta", length = 20, nullable = false)
    @Pattern(regexp = "in_attesa|accettata|rifiutata")
    private String statoOfferta;

    @Column(name = "data_offerta", nullable = false)
    private OffsetDateTime dataOfferta = OffsetDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String note;


    //Getter e Setter per il momento eliminati


    //Costruttori


    public Offerta() {
    }


    public Offerta(Long idOfferta, Immobile immobile, Utente cliente, Agente agente, BigDecimal prezzoOfferta, String statoOfferta, OffsetDateTime dataOfferta, String note) {
        this.idOfferta = idOfferta;
        this.immobile = immobile;
        this.cliente = cliente;
        this.agente = agente;
        this.prezzoOfferta = prezzoOfferta;
        this.statoOfferta = statoOfferta;
        this.dataOfferta = dataOfferta;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "idOfferta=" + idOfferta +
                ", immobileId=" + (immobile != null ? immobile.getIdImmobile() : null) +
                ", clienteId=" + (cliente != null ? cliente.getIdUtente() : null) +
                ", agenteId=" + (agente != null ? agente.getIdAgente() : null) +
                ", prezzoOfferta=" + prezzoOfferta +
                ", statoOfferta='" + statoOfferta + '\'' +
                ", dataOfferta=" + dataOfferta +
                ", note='" + note + '\'' +
                '}';
    }
}
