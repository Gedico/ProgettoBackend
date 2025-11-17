package ProgettoINSW.backend.util;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.InserzioneRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidazioneUtil {

    private final AccountRepository accountRepository;
    private final InserzioneRepository inserzioneRepository;

    public ValidazioneUtil(AccountRepository accountRepository, InserzioneRepository inserzioneRepository) {
        this.accountRepository = accountRepository;
        this.inserzioneRepository = inserzioneRepository;
    }

    /*
     * ✅ Verifica se l'utente autenticato è l'agente proprietario dell'inserzione
     *     oppure un amministratore autorizzato a modificarla.
     *
     * @param idInserzione id dell'inserzione
     * @param token JWT di autenticazione
     * @return true se l'utente può modificare, false altrimenti
     */
    public boolean verificaAgenteInserzione(Long idInserzione, String token) {
        String mailAgente = JwtUtil.extractMail(token);

        Inserzione inserzione = inserzioneRepository.findById(idInserzione)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        Account accountRichiedente = accountRepository.findByMailIgnoreCase(mailAgente)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = inserzione.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        return !isAdmin && !isProprietario;
    }
}

