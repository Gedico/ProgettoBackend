package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.profilo.*;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.service.ProfiloService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfiloServiceImpl implements ProfiloService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfiloServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ProfiloResponse getProfilo(String mail) {
        Account account = accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        ProfiloResponse response = new ProfiloResponse();
        response.setNome(account.getNome());
        response.setCognome(account.getCognome());
        response.setMail(account.getMail());
        response.setNumero(account.getNumero());
        response.setRuolo(account.getRuolo());

        return response;
    }

    @Override
    public UpdateProfiloResponse aggiornaProfilo(UpdateProfiloRequest request, String mail) {
        Account account = accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        if (request.getNome() != null) account.setNome(request.getNome());
        if (request.getCognome() != null) account.setCognome(request.getCognome());
        if (request.getNumero() != null) account.setNumero(request.getNumero());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        accountRepository.save(account);

        return new UpdateProfiloResponse("Profilo aggiornato con successo", true);
    }
}
