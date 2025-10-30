package ProgettoINSW.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "FotoImmobili")
public class FotoImmobili {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobile", nullable = false)
    @JsonBackReference
    private Immobile immobile;

    @Column(name = "url_foto", length = 500, nullable = false, unique = true)
    @NotBlank
    private String urlFoto;

    //Costruttori

    public FotoImmobili() {
    }


    public FotoImmobili(Long idFoto, Immobile immobile, String urlFoto) {
        this.idFoto = idFoto;
        this.immobile = immobile;
        this.urlFoto = urlFoto;
    }
}
