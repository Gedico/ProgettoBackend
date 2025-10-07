package ProgettoINSW.backend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "offerta")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
