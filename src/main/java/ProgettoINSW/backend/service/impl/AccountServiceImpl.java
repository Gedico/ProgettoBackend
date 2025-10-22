package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.UtenteRepository;
import ProgettoINSW.backend.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, UtenteRepository utenteRepository,PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponseUtente registraUtente(RegisterRequestUtente request){

        if(accountRepository.existsByMailIgnoreCase(request.getMail())){
            throw new RuntimeException("Email già registrata");//Possibile exception personalizzata nelle exception
        }

        Account account = new Account();
        account.setNome(request.getNome());
        account.setCognome(request.getCognome());
        account.setMail(request.getMail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setNumero(request.getNumero());
        account.setRuolo(Role.UTENTE);

        Account savedAccount = accountRepository.save(account);

        Utente utente = new Utente();
        utente.setAccount(savedAccount);//Foreign key
        utente.setIndirizzo(request.getIndirizzo());
        utente.setDataIscrizione(LocalDateTime.now());
        
        utenteRepository.save(utente);

        RegisterResponseUtente response = new RegisterResponseUtente();
        response.setIdAccount(savedAccount.getId_account());
        response.setNome(savedAccount.getNome());
        response.setCognome(savedAccount.getCognome());
        response.setMail(savedAccount.getMail());
        response.setRuolo(savedAccount.getRuolo());
        response.setIndirizzo(utente.getIndirizzo());

        response.setNumero(savedAccount.getNumero());
        response.setMessaggio(request.getMessaggio());
        
        
        return response;
    }

    @Override
    public void eliminaAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account non trovato con ID: " + id));

        accountRepository.delete(account);
    }


}
