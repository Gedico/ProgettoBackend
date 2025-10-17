package ProgettoINSW.backend.service;


import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;

public interface AccountService {

    RegisterResponseUtente registraUtente(RegisterRequestUtente utente);

}
