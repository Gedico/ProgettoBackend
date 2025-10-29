package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.registrazione.RegisterRequest;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.model.enums.Role;
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

    // Endpoint per registrazione utente
    @PostMapping("/registerUtente")
    public ResponseEntity<RegisterResponse> registraUtente(@RequestBody RegisterRequest request) {
        RegisterResponse response = accountService.registraAccount(request,Role.UTENTE);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint per registrazione Agente
    @PostMapping("/registerAgente")
    public ResponseEntity<RegisterResponse> registraAgente(@RequestBody RegisterRequest request) {
        RegisterResponse response = accountService.registraAccount(request,Role.AGENTE);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint per registrazione Admin
    @PostMapping("/registerAdmin")
    public ResponseEntity<RegisterResponse> registraAdmin(@RequestBody RegisterRequest request) {
        RegisterResponse response = accountService.registraAccount(request,Role.ADMIN);
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

    // Endpoint per logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String message = accountService.logout(token);
        return ResponseEntity.ok(message);
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

   // Endpoint per profilo (recupero dati)
   /*@GetMapping("/profile")
   public ResponseEntity<RegisterResponse> getProfile(@RequestHeader("Authorization") String token) {
       RegisterResponse response = accountService.getProfile(token);
       return ResponseEntity.ok(response);
   }*/


}
