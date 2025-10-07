package ProgettoINSW.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.math.BigDecimal;


@Entity
@Table(name = "immobile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Immobile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_immobile")
    private Long idImmobile;

    // Relazione con agente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente", nullable = false)
    private Agente agente;

    // Relazione con posizione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_posizione", nullable = false)
    private Posizione posizione;

    @Column(length = 150, nullable = false)
    @NotBlank
    private String titolo;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione = OffsetDateTime.now();

    @Column(length = 1000)
    private String descrizione;

    @Column(precision = 12, scale = 2, nullable = false)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal prezzo;

    @Column
    @Min(1)
    private Integer dimensioni;

    @Column(name = "numero_stanze")
    @Min(2)
    private Integer numeroStanze;

    @Column
    private Integer piano;

    @Column(nullable = false)
    private Boolean ascensore = false;

    @Column(name = "classe_energetica", length = 10)
    private String classeEnergetica;

    @Column(length = 10, nullable = false)
    @Pattern(regexp = "vendita|affitto")
    private String categoria;



    // Costruttore parziale con campi obbligatori
    public Immobile(Agente agente, Posizione posizione, String titolo,
                    BigDecimal prezzo, String categoria) {
        this.agente = agente;
        this.posizione = posizione;
        this.titolo = titolo;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.dataCreazione = OffsetDateTime.now();
        this.ascensore = false;
    }


    @Override
    public String toString() {
        return "Immobile{" +
                "idImmobile=" + idImmobile +
                ", idAgente=" + (agente != null ? agente.getIdAgente() : null) +
                ", idPosizione=" + (posizione != null ? posizione.getIdPosizione() : null) +
                ", titolo='" + titolo + '\'' +
                ", dataCreazione=" + dataCreazione +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                ", dimensioni=" + dimensioni +
                ", numeroStanze=" + numeroStanze +
                ", piano=" + piano +
                ", ascensore=" + ascensore +
                ", classeEnergetica='" + classeEnergetica + '\'' +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
