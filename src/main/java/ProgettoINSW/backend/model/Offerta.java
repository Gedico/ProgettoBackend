package ProgettoINSW.backend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    @Column(name = "prezzo_offerta", precision = 12, scale = 2, nullable = false)
    @DecimalMin("0.0")
    private BigDecimal prezzoOfferta;

    @Column(name = "stato_offerta", length = 20, nullable = false)
    @Pattern(regexp = "in_attesa|accettata|rifiutata")
    private String statoOfferta;

    @Column(name = "data_offerta", nullable = false)
    private OffsetDateTime dataOfferta = OffsetDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String note;

    public Offerta() { }

    public Offerta(Immobile immobile, Utente cliente, Agente agente,
                   BigDecimal prezzoOfferta, String statoOfferta,
                   OffsetDateTime dataOfferta, String note) {
        this.immobile = immobile;
        this.cliente = cliente;
        this.agente = agente;
        this.prezzoOfferta = prezzoOfferta;
        this.statoOfferta = statoOfferta;
        this.dataOfferta = dataOfferta != null ? dataOfferta : OffsetDateTime.now();
        this.note = note;
    }
    public Long getIdOfferta() { return idOfferta; }
    public void setIdOfferta(Long idOfferta) { this.idOfferta = idOfferta; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

    public Utente getCliente() { return cliente; }
    public void setCliente(Utente cliente) { this.cliente = cliente; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public BigDecimal getPrezzoOfferta() { return prezzoOfferta; }
    public void setPrezzoOfferta(BigDecimal prezzoOfferta) { this.prezzoOfferta = prezzoOfferta; }

    public String getStatoOfferta() { return statoOfferta; }
    public void setStatoOfferta(String statoOfferta) { this.statoOfferta = statoOfferta; }

    public OffsetDateTime getDataOfferta() { return dataOfferta; }
    public void setDataOfferta(OffsetDateTime dataOfferta) { this.dataOfferta = dataOfferta; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

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
