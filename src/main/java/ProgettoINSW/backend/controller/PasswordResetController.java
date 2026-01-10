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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

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



    @PostMapping("/reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {

        Optional<Account> accountOpt = accountRepository.findByMail(request.getEmail());

        // 1. Se l'email non esiste → rispondi comunque 200
        if (accountOpt.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "Se l'email è corretta, riceverai un link per il reset.")
            );
        }

        Account account = accountOpt.get();

        // 2. Genera nuovo token
        String token = UUID.randomUUID().toString();

        // 3. Se esiste già un token per quell'account, lo aggiorno (refresh); altrimenti lo creo
        PasswordResetToken resetToken = passwordResetTokenRepository.findByAccount(account)
                .orElseGet(PasswordResetToken::new);

        resetToken.setAccount(account);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepository.save(resetToken);

        // 4. Invia email
        String link = "http://localhost:4200/#/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(account.getMail(), link);

        // 5. Risposta uniforme e sempre 200 OK
        return ResponseEntity.ok(
                Map.of("message", "Se l'email è corretta, riceverai un link per il reset.")
        );
    }



    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        Optional<PasswordResetToken> tokenOpt =
                passwordResetTokenRepository.findByToken(request.getToken());

        // Caso 1 → token mancante o non valido
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("status", "invalid_token", "message", "Token non valido o non esistente.")
            );
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Caso 2 → token scaduto
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.ok(
                    Map.of("status", "expired", "message", "Token scaduto.")
            );
        }

        Account account = resetToken.getAccount();

        // Aggiorno password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);

        // Elimino il token
        passwordResetTokenRepository.delete(resetToken);

        // Caso 3 → successo
        return ResponseEntity.ok(
                Map.of("status", "success", "message", "Password aggiornata con successo")
        );
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.ok(Map.of("status", "invalid_token", "message", "Token mancante."));
        }

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "invalid_token", "message", "Token non valido."));
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.ok(Map.of("status", "expired", "message", "Token scaduto."));
        }

        return ResponseEntity.ok(Map.of("status", "success", "message", "Token valido."));
    }



}

