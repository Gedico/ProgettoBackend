package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.registrazione.RegisterRequestUtente;
import ProgettoINSW.backend.dto.registrazione.RegisterResponseUtente;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.UtenteRepository;
import ProgettoINSW.backend.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ProgettoINSW.backend.util.JwtUtil;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponseUtente registraUtente(RegisterRequestUtente request) {

        if (accountRepository.existsByMailIgnoreCase(request.getMail())) {
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
        response.setIdAccount(savedAccount.getId());
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
    public LoginResponse loginUtente(LoginRequest request) {

        // Trova l’account tramite la mail
        Account account = accountRepository.findByMailIgnoreCase(request.getMail())
                .orElse(null);

        if (account == null) {
            return new LoginResponse("Nessun account trovato con questa mail.", null, null);
        }

        // Verifica la password criptata
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            return new LoginResponse("Password errata.", null, null);
        }

        // Genera token JWT
        String token = JwtUtil.generateToken(account.getMail(), account.getRuolo().name());

        // Crea e restituisce la risposta
        return new LoginResponse(
                "Login effettuato con successo.",
                account.getRuolo().name(),
                token
        );
    }

    @Override
    public String logout(String token) {
        if (token == null || token.isEmpty()) {
            return "Token non fornito.";
        }

        // Se inizia con 'Bearer ', lo puliamo
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Verifica se il token è valido (così non “logout” token già scaduti)
        boolean valido = JwtUtil.validateToken(token);
        if (!valido) {
            return "Token non valido o già scaduto.";
        }

        // Logout simbolico: il frontend eliminerà il token
        return "Logout effettuato con successo. Token non più utilizzabile.";
    }


    @Override
    public void eliminaAccount(Long idAccount) {
        // Verifica che l’account esista
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> new RuntimeException("Account non trovato con ID: " + idAccount));

        // Elimina prima l’utente collegato (se esiste)
        utenteRepository.findByAccount_Id(idAccount).ifPresent(utenteRepository::delete);

        // Poi elimina l’account
        accountRepository.delete(account);
    }


}

