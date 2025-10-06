package ProgettoINSW.backend.model;

import jakarta.persistence.*;

public class account {

    @Entity
    @Table(name = "account")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_account;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String numero;

}
