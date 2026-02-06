package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.recuperopassword.PasswordResetRequest;
import ProgettoINSW.backend.dto.recuperopassword.ResetPasswordRequest;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.PasswordResetTokenRepository;
import ProgettoINSW.backend.security.PasswordResetToken;
import ProgettoINSW.backend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    /* =========================
       COSTANTI RISPOSTA API
       ========================= */
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";

    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_INVALID = "invalid_token";
    private static final String STATUS_EXPIRED = "expired";

    /* =========================
       DIPENDENZE
       ========================= */
    private final AccountRepository accountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetController(AccountRepository accountRepository,
                                   PasswordResetTokenRepository passwordResetTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService) {
        this.accountRepository = accountRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /* =========================
       METODO HELPER RISPOSTE
       ========================= */
    private ResponseEntity<Map<String, String>> response(String status, String message) {
        return ResponseEntity.ok(
                Map.of(
                        STATUS_KEY, status,
                        MESSAGE_KEY, message
                )
        );
    }

    /* =========================
       RICHIESTA RESET PASSWORD
       ========================= */
    @PostMapping("/reset-request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @RequestBody PasswordResetRequest request) {

        Optional<Account> accountOpt =
                accountRepository.findByMail(request.getEmail());

        // Risposta uniforme per evitare enumeration attack
        if (accountOpt.isEmpty()) {
            return response(
                    STATUS_SUCCESS,
                    "Se l'email è corretta, riceverai un link per il reset."
            );
        }

        Account account = accountOpt.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByAccount(account)
                        .orElseGet(PasswordResetToken::new);

        resetToken.setAccount(account);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepository.save(resetToken);

        String link =
                "http://localhost:4200/#/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(account.getMail(), link);

        return response(
                STATUS_SUCCESS,
                "Se l'email è corretta, riceverai un link per il reset."
        );
    }

    /* =========================
       RESET PASSWORD
       ========================= */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        Optional<PasswordResetToken> tokenOpt =
                passwordResetTokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return response(
                    STATUS_INVALID,
                    "Token non valido o non esistente."
            );
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return response(
                    STATUS_EXPIRED,
                    "Token scaduto."
            );
        }

        Account account = resetToken.getAccount();

        String encodedPassword =
                passwordEncoder.encode(request.getNewPassword());

        account.setPassword(encodedPassword);
        accountRepository.save(account);

        passwordResetTokenRepository.delete(resetToken);

        return response(
                STATUS_SUCCESS,
                "Password aggiornata con successo"
        );
    }

    /* =========================
       VERIFICA TOKEN RESET
       ========================= */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(
            @RequestBody Map<String, String> body) {

        String token = body.get("token");

        if (token == null || token.isBlank()) {
            return response(
                    STATUS_INVALID,
                    "Token mancante."
            );
        }

        Optional<PasswordResetToken> tokenOpt =
                passwordResetTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return response(
                    STATUS_INVALID,
                    "Token non valido."
            );
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return response(
                    STATUS_EXPIRED,
                    "Token scaduto."
            );
        }

        return response(
                STATUS_SUCCESS,
                "Token valido."
        );
    }
}


