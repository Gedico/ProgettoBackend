package ProgettoINSW.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "immobile")
public class Immobile {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_immobile")
    private Long idImmobile;

    // Relazione con agente
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente", nullable = false)
    private Agente agente;

    // Relazione con posizione
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_posizione", nullable = false)
    private Posizione posizione;

    // Relazione con FotoImmobile
    @OneToMany(mappedBy = "immobile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoImmobili> fotoImmobili = new ArrayList<>();

    @Setter
    @Getter
    @Column(length = 150, nullable = false)
    @NotBlank
    private String titolo;

    @Getter
    @Setter
    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione = OffsetDateTime.now();

    @Setter
    @Getter
    @Column(length = 1000)
    private String descrizione;

    @Getter
    @Setter
    @Column( nullable = false)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal prezzo;

    @Getter
    @Setter
    @Column
    @Min(1)
    private Integer dimensioni;

    @Getter
    @Setter
    @Column(name = "numero_stanze")
    @Min(2)
    private Integer numeroStanze;

    @Getter
    @Setter
    @Column
    private Integer piano;

    @Getter
    @Setter
    @Column(nullable = false)
    private Boolean ascensore = false;

    @Getter
    @Setter
    @Column(name = "classe_energetica", length = 10)
    private String classeEnergetica;

    @Setter
    @Getter
    @Column(length = 10, nullable = false)
    @Pattern(regexp = "vendita|affitto")
    private String categoria;


    //getter e Setter


    //Costruttori


    public Immobile() {
    }

    public Immobile(Long idImmobile, Agente agente, Posizione posizione, String titolo, OffsetDateTime dataCreazione, String descrizione, BigDecimal prezzo, Integer dimensioni, Integer numeroStanze, Integer piano, Boolean ascensore, String classeEnergetica, String categoria) {
        this.idImmobile = idImmobile;
        this.agente = agente;
        this.posizione = posizione;
        this.titolo = titolo;
        this.dataCreazione = dataCreazione;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.dimensioni = dimensioni;
        this.numeroStanze = numeroStanze;
        this.piano = piano;
        this.ascensore = ascensore;
        this.classeEnergetica = classeEnergetica;
        this.categoria = categoria;
    }

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
