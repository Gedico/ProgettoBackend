package ProgettoINSW.backend.model;


import ProgettoINSW.backend.model.enums.Categoria;
import ProgettoINSW.backend.model.enums.StatoInserzione;
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
@Table(name = "inserzione")
public class Inserzione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inserzione")
    private Long idInserzione;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente", nullable = false)
    private Agente agente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_posizione", nullable = false)
    private Posizione posizione;


    @OneToMany(mappedBy = "inserzione", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> foto = new ArrayList<>();

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
    @Column(name = "categoria" , nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false, length = 20)
    private StatoInserzione stato = StatoInserzione.DISPONIBILE;

    //Costruttori


    public Inserzione() {
    }

    @Override
    public String toString() {
        return "Inserzione{" +
                "idInserzione=" + idInserzione +
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
