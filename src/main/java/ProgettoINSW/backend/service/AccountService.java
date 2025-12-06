package ProgettoINSW.backend.service;


import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.dto.logout.LogoutResponse;
import ProgettoINSW.backend.dto.registrazione.RegisterRequest;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import ProgettoINSW.backend.model.enums.Role;

public interface AccountService {

    RegisterResponse registraAccount(RegisterRequest account, Role ruolo);
    LoginResponse loginUtente(LoginRequest request);
    LogoutResponse logout(String token);
    void eliminaAccount(Long id_account);

}