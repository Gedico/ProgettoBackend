package ProgettoINSW.backend.model;


import ProgettoINSW.backend.model.enums.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "immobile")
public class Immobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_immobile")
    private Long idImmobile;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente", nullable = false)
    private Agente agente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_posizione", nullable = false)
    private Posizione posizione;


    @OneToMany(mappedBy = "immobile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoImmobili> fotoImmobili = new ArrayList<>();

    @Column(name = "titolo", length = 150, nullable = false)
    @NotBlank
    private String titolo;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione = OffsetDateTime.now();

    @Column(name = "descrizione",length = 1000)
    private String descrizione;

    @Column(name = "prezzo", nullable = false)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal prezzo;

    @Column(name = "dimensioni")
    @Min(1)
    private Integer dimensioni;

    @Column(name = "numero_stanze")
    @Min(2)
    private Integer numeroStanze;

    @Column(name = "piano")
    private Integer piano;

    @Column(name = "ascensore",nullable = false)
    private Boolean ascensore = false;

    @Column(name = "classe_energetica", length = 10)
    private String classeEnergetica;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Categoria categoria;



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
        this.categoria = Categoria.valueOf(categoria);
    }

    // Costruttore parziale con campi obbligatori
    public Immobile(Agente agente, Posizione posizione, String titolo,
                    BigDecimal prezzo, String categoria) {
        this.agente = agente;
        this.posizione = posizione;
        this.titolo = titolo;
        this.prezzo = prezzo;
        this.categoria = Categoria.valueOf(categoria);
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
