package ProgettoINSW.backend.service;


import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;

public interface AccountService {

    RegisterResponseUtente registraUtente(RegisterRequestUtente utente);
    LoginResponse loginUtente(LoginRequest request);
    void eliminaAccount(Long id_account);

}
