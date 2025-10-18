package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;
import ProgettoINSW.backend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
