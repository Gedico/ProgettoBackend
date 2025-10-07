package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agente")
@Data// CREA GETTER E SETTER
@NoArgsConstructor// CREA COSTRUTTORE VUOTO
@AllArgsConstructor//CREA COSTRUTTORE CON TUTTI I PARAMETRI

public class Indicatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agente")
    private Long idIndicatore;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoIndicatore tipo;

    @Column(name = "distanza", precision = 6, scale = 2)
    private Double distanza;

    @ManyToOne
    @JoinColumn(name = "id_inserzione", nullable = false,
            foreignKey = @ForeignKey(name = "fk_indicatore_inserzione"))
    private Immobile immobile;

    public enum TipoIndicatore {
        SCUOLA, PARCO, CENTRO, RISTORANTE, ALTRO
    }

}
