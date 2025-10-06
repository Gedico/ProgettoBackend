package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agente")
@Data// CREA GETTER E SETTER
@NoArgsConstructor// CREA COSTRUTTORE VUOTO
@AllArgsConstructor//CREA COSTRUTTORE CON TUTTI I PARAMETRI
public class Agente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agente")
    private Long idAgente;

    @OneToOne
    @JoinColumn(name = "id_account", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_agente_account"))
    private Account account;

    @Column(name = "agenzia", length = 255)
    private String agenzia;
}
