package ProgettoINSW.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inserzione", nullable = false)
    @JsonBackReference
    private Inserzione inserzione;

    @Column(name = "url_foto", length = 500, nullable = false, unique = true)
    @NotBlank
    private String urlFoto;

    //Costruttori

    public Foto() {
    }


    public Foto(Long idFoto, Inserzione inserzione, String urlFoto) {
        this.idFoto = idFoto;
        this.inserzione = inserzione;
        this.urlFoto = urlFoto;
    }
}
