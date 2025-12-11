package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.AgenteRepository;
import ProgettoINSW.backend.util.JwtUtil;
import ProgettoINSW.backend.service.AgenteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgenteServiceImpl implements AgenteService {

    private final AccountRepository accountRepository;
    private final AgenteRepository agenteRepository;

    @Override
    public Account getAccountFromToken(String token) {

        String mail = JwtUtil.extractMail(token);

        return accountRepository.findByMail(mail)
                .orElseThrow(() ->
                        new EntityNotFoundException("Account non trovato per email: " + mail));
    }

    @Override
    public Agente getAgenteFromToken(String token) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() ->
                        new EntityNotFoundException("Account non trovato per email: " + mail));

        return agenteRepository.findByAccount(account)
                .orElseThrow(() ->
                        new EntityNotFoundException("Agente non presente per account: " + mail));
    }
}

