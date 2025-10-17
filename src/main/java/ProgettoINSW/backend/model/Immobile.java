package ProgettoINSW.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "immobile")
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

    // Relazione con FotoImmobile
    @OneToMany(mappedBy = "immobile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoImmobili> fotoImmobili = new ArrayList<>();

    @Column(length = 150, nullable = false)
    @NotBlank
    private String titolo;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione = OffsetDateTime.now();

    @Column(length = 1000)
    private String descrizione;

    @Column( nullable = false)
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


    //getter e Setter


    public Long getIdImmobile() {
        return idImmobile;
    }

    public void setIdImmobile(Long idImmobile) {
        this.idImmobile = idImmobile;
    }

    public Agente getAgente() {
        return agente;
    }

    public void setAgente(Agente agente) {
        this.agente = agente;
    }

    public Posizione getPosizione() {
        return posizione;
    }

    public void setPosizione(Posizione posizione) {
        this.posizione = posizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public OffsetDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(OffsetDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public Integer getDimensioni() {
        return dimensioni;
    }

    public void setDimensioni(Integer dimensioni) {
        this.dimensioni = dimensioni;
    }

    public Integer getNumeroStanze() {
        return numeroStanze;
    }

    public void setNumeroStanze(Integer numeroStanze) {
        this.numeroStanze = numeroStanze;
    }

    public Integer getPiano() {
        return piano;
    }

    public void setPiano(Integer piano) {
        this.piano = piano;
    }

    public Boolean getAscensore() {
        return ascensore;
    }

    public void setAscensore(Boolean ascensore) {
        this.ascensore = ascensore;
    }

    public String getClasseEnergetica() {
        return classeEnergetica;
    }

    public void setClasseEnergetica(String classeEnergetica) {
        this.classeEnergetica = classeEnergetica;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


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
