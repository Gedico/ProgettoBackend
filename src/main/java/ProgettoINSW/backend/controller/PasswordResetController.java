package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.recuperopassword.PasswordResetRequest;
import ProgettoINSW.backend.dto.recuperopassword.ResetPasswordRequest;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.PasswordResetTokenRepository;
import ProgettoINSW.backend.security.PasswordResetToken;
import ProgettoINSW.backend.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
        if (accountOpt.isEmpty()) {
            // in produzione potresti comunque rispondere 200 per non far capire se l'email esiste
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Nessun account trovato con questa email");
        }

        Account account = accountOpt.get();

        // Genera token random sicuro
        String token = UUID.randomUUID().toString();

        // Crea e salva token con scadenza
        PasswordResetToken resetToken = new PasswordResetToken(token, account);
        passwordResetTokenRepository.save(resetToken);

        String link = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(account.getMail(), link);


        // In futuro: qui invierai la mail vera
        return ResponseEntity.ok("Se l'email è corretta, è stato inviato un link per il reset.");
    }


    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        Optional<PasswordResetToken> tokenOpt =
                passwordResetTokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token non valido");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Controllo scadenza
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token scaduto");
        }

        Account account = resetToken.getAccount();

        // Aggiorno password con BCrypt
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);

        // Token non più riutilizzabile
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password aggiornata con successo");
    }



}

