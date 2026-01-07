package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.security.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByAccount(Account account);
}

