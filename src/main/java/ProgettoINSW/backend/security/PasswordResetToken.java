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

    private String token;

    @OneToOne
    private Account account;

    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, Account account) {
        this.token = token;
        this.account = account;
        this.expiryDate = LocalDateTime.now().plusMinutes(30); // token valido 30 min
    }

    public PasswordResetToken() {}
}

