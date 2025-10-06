package ProgettoINSW.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "posizione")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posizione")
    private Long idPosizione;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitudine;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitudine;

    @Column(length = 255)
    private String descrizione;


}

