package ProgettoINSW.backend.model;

import jakarta.persistence.*;


public class utente {

    @Entity
    @Table(name = "utente")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_utente;

    @Column(nullable = false, unique = true)


}
