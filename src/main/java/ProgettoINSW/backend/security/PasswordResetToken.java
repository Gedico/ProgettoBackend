package ProgettoINSW.backend.security;

import ProgettoINSW.backend.model.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id_account", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, Account account) {
        this.token = token;
        this.account = account;
        this.expiryDate = LocalDateTime.now().plusMinutes(10); // token valido 10 min
    }

    public PasswordResetToken() {}
}

