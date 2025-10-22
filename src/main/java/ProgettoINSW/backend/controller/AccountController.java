package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Endpoint per registrazione
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseUtente> registerUser(@RequestBody RegisterRequestUtente request) {
        RegisterResponseUtente response = accountService.registraUtente(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint per login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = accountService.loginUtente(request);

        if ("Nessun account trovato con questa mail.".equals(response.getMessaggio()) ||
                "Password errata.".equals(response.getMessaggio())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // Endpoint per l'eliminazione
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        try {
            accountService.eliminaAccount(id);
            return ResponseEntity.ok("Account eliminato con successo!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}
