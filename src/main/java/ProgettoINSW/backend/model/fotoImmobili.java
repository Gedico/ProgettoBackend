package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "FotoImmobili")
public class fotoImmobili {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobile", nullable = false)
    private immobile immobile;

    @Column(name = "url_foto", length = 500, nullable = false, unique = true)
    @NotBlank
    private String urlFoto;

    public FotoImmobili() { }

    // Costruttore completo (senza idFoto, perché generato automaticamente)
    public FotoImmobili(immobile immobile, String urlFoto) {
        this.immobile = immobile;
        this.urlFoto = urlFoto;
    }

    // ===========================
    // GETTER e SETTER
    // ===========================
    public Long getIdFoto() { return idFoto; }
    public void setIdFoto(Long idFoto) { this.idFoto = idFoto; }

    public immobile getImmobile() { return immobile; }
    public void setImmobile(immobile immobile) { this.immobile = immobile; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }


    @Override
    public String toString() {
        return "FotoImmobili{" +
                "idFoto=" + idFoto +
                ", immobileId=" + (immobile != null ? immobile.getIdImmobile() : null) +
                ", urlFoto='" + urlFoto + '\'' +
                '}';
    }

}
