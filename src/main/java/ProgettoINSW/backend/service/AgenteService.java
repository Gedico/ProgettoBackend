package ProgettoINSW.backend.service;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;

public interface AgenteService {

    Account getAccountFromToken(String token);

    Agente getAgenteFromToken(String token);
}
