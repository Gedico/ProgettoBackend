package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;
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


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminaAccount(@PathVariable Long id) {
        try {
            accountService.eliminaAccount(id);
            return ResponseEntity.ok("Account eliminato con successo (ID: " + id + ")");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Errore: " + e.getMessage());
        }
    }
}
