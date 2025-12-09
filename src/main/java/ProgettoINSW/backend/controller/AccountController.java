package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.registrazione.RegisterRequest;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import ProgettoINSW.backend.dto.logout.LogoutResponse;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.dto.response.SimpleResponse;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.service.AccountService;
import jakarta.validation.Valid;
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


/******REGISTRAZIONI************************************************************************************************************/

    @PostMapping("/registerUtente")
    public ResponseEntity<RegisterResponse> registraUtente(@Valid @RequestBody RegisterRequest request) {
        return registra(request, Role.UTENTE);
    }

    @PostMapping("/registerAgente")
    public ResponseEntity<RegisterResponse> registraAgente(@Valid @RequestBody RegisterRequest request) {
        return registra(request, Role.AGENTE);
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<RegisterResponse> registraAdmin(@Valid @RequestBody RegisterRequest request) {
        return registra(request, Role.ADMIN);
    }



/*********LOGIN & LOGOUT*********************************************************************************************************/

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = accountService.loginUtente(request);

        if (response.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        LogoutResponse response = accountService.logout(token);

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }



/**********ELIMINAZIONE ACOUNT********************************************************************************************************/

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SimpleResponse> deleteAccount(@PathVariable Long id) {
        accountService.eliminaAccount(id);
        return ResponseEntity.ok(new SimpleResponse(true, "Account eliminato con successo"));
    }



/**********FUNZIONI AUSILIARIE*************************************************************************************************************/

    private ResponseEntity<RegisterResponse> registra(RegisterRequest request, Role role) {
        RegisterResponse response = accountService.registraAccount(request, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
