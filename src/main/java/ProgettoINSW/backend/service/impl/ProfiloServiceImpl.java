package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.profilo.*;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.UtenteRepository;
import ProgettoINSW.backend.service.ProfiloService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfiloServiceImpl implements ProfiloService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private final UtenteRepository utenteRepository;

    public ProfiloServiceImpl(AccountRepository accountRepository, UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ProfiloResponse getProfilo(String mail) {

        Account account = accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        Utente utente = utenteRepository.findByAccount_Id(account.getId())
                .orElse(null);

        ProfiloResponse response = new ProfiloResponse();
        response.setNome(account.getNome());
        response.setCognome(account.getCognome());
        response.setMail(account.getMail());
        response.setNumero(account.getNumero());
        response.setRuolo(account.getRuolo());

        response.setIndirizzo(utente != null ? utente.getIndirizzo() : null);

        return response;
    }


    @Override
    public UpdateProfiloResponse aggiornaProfilo(UpdateProfiloRequest request, String mail) {

        Account account = accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        // aggiornamento campi in account
        if (request.getNome() != null) account.setNome(request.getNome());
        if (request.getCognome() != null) account.setCognome(request.getCognome());
        if (request.getNumero() != null) account.setNumero(request.getNumero());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        accountRepository.save(account);

        Utente utente = utenteRepository.findByAccount_Id(account.getId()).orElse(null);

        if (utente == null) {
            utente = new Utente();
            utente.setAccount(account);
            utente.setDataIscrizione(LocalDateTime.now());
        }

        if (request.getIndirizzo() != null) {
            utente.setIndirizzo(request.getIndirizzo());
        }

        utenteRepository.save(utente);

        return new UpdateProfiloResponse("Profilo aggiornato con successo", true);
    }

    @Override
    public void changePassword(String mail, ChangePasswordRequest request) {

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new RuntimeException("Password attuale non corretta");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }



}
