package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.registrazione.RegisterRequest;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.AgenteRepository;
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
    private final AgenteRepository agenteRepository;

    public AccountServiceImpl(AccountRepository accountRepository, AgenteRepository agenteRepository  , UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
        this.agenteRepository = agenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse registraAccount(RegisterRequest request, Role ruolo) {

        //Controllo unicità email
        if (accountRepository.existsByMailIgnoreCase(request.getMail())) {
            throw new RuntimeException("Email già registrata");
            // creare una exception personalizzata nelle exception/
        }

        // Creazione Account base
        Account account = new Account();
        account.setNome(request.getNome());
        account.setCognome(request.getCognome());
        account.setMail(request.getMail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setNumero(request.getNumero());
        account.setRuolo(ruolo);

        Account savedAccount = accountRepository.save(account);

        //specifiche in base al ruolo
        switch (ruolo) {
            case UTENTE -> {
                Utente utente = new Utente();
                utente.setAccount(savedAccount);
                utente.setIndirizzo(request.getApprofondimento()); // 👈 indirizzo
                utente.setDataIscrizione(LocalDateTime.now());
                utenteRepository.save(utente);
            }

            case AGENTE -> {
                Agente agente = new Agente();
                agente.setAccount(savedAccount);
                agente.setAgenzia(request.getApprofondimento()); // 👈 agenzia
                agenteRepository.save(agente);
            }

            case ADMIN -> {
                //Admin non ha tabella
            }

            default -> throw new IllegalArgumentException("Ruolo non supportato: " + ruolo);
        }

        //Creazione risposta
        RegisterResponse response = new RegisterResponse();
        response.setIdAccount(savedAccount.getId());
        response.setNome(savedAccount.getNome());
        response.setCognome(savedAccount.getCognome());
        response.setMail(savedAccount.getMail());
        response.setNumero(savedAccount.getNumero());
        response.setRuolo(savedAccount.getRuolo());
        response.setMessaggio(request.getMessaggio() != null ? request.getMessaggio() : "Registrazione completata");

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

   /* @Override
    public RegisterResponse getProfile(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token non fornito o non valido.");
        }

        token = token.substring(7); // Rimuove "Bearer "

        String email = JwtUtil.extractMail(token);
        Account account = accountRepository.findByMailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Account non trovato per questa email."));

        Utente utente = utenteRepository.findByAccount_Id(account.getId())
                .orElse(null);

        RegisterResponse response = new RegisterResponse();
        response.setIdAccount(account.getId());
        response.setNome(account.getNome());
        response.setCognome(account.getCognome());
        response.setMail(account.getMail());
        response.setNumero(account.getNumero());
        response.setRuolo(account.getRuolo());
        if (utente != null) {
            response.setIndirizzo(utente.getIndirizzo());
        }

        response.setMessaggio("Profilo recuperato con successo.");

        return response;
    }*/  //Credo che qui vada usato un dto diverso da quello usato per la registrazione a questo punto

}