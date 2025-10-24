package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "agente")

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


    //Getter e Setter


    //Costruttori


    public Agente() {
    }

    public Agente(Long idAgente, Account account, String agenzia) {
        this.idAgente = idAgente;
        this.account = account;
        this.agenzia = agenzia;
    }
}
