package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "FotoImmobili")
public class FotoImmobili {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobile", nullable = false)
    private Immobile immobile;

    @Column(name = "url_foto", length = 500, nullable = false, unique = true)
    @NotBlank
    private String urlFoto;

    public void FotoImmobili() { }

    // Costruttore completo (senza idFoto, perché generato automaticamente)
    public void FotoImmobili(Immobile immobile, String urlFoto) {
        this.immobile = immobile;
        this.urlFoto = urlFoto;
    }

    // ===========================
    // GETTER e SETTER
    // ===========================
    public Long getIdFoto() { return idFoto; }
    public void setIdFoto(Long idFoto) { this.idFoto = idFoto; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

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
