package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.logout.LogoutResponse;
import ProgettoINSW.backend.dto.registrazione.RegisterRequest;
import ProgettoINSW.backend.dto.registrazione.RegisterResponse;
import ProgettoINSW.backend.dto.login.LoginRequest;
import ProgettoINSW.backend.dto.login.LoginResponse;
import ProgettoINSW.backend.exception.BusinessException;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.AgenteRepository;
import ProgettoINSW.backend.repository.UtenteRepository;
import ProgettoINSW.backend.service.AccountService;
import jakarta.transaction.Transactional;
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


/******REGISTRAZIONI************************************************************************************************************/

    @Transactional
    @Override
    public RegisterResponse registraAccount(RegisterRequest request, Role ruolo) {

        if (accountRepository.existsByMailIgnoreCase(request.getMail())) {
            throw new BusinessException("Email già registrata");
        }

        Account account = createAccount(request, ruolo);
        Account savedAccount = accountRepository.save(account);

        createLinkedEntity(savedAccount, request, ruolo);

        return buildRegisterResponse(savedAccount, request);
    }


/*********LOGIN & LOGOUT*********************************************************************************************************/

    @Override
    public LoginResponse loginUtente(LoginRequest request) {

        Account account = accountRepository.findByMailIgnoreCase(request.getMail())
                .orElseThrow(() -> new BusinessException("Nessun account trovato con questa mail."));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new BusinessException("Password errata.");
        }

        String token = JwtUtil.generateToken(account.getMail(), account.getRuolo().name());

        return buildLoginResponse(account,token);
    }


    @Override
    public LogoutResponse logout(String token) {

        LogoutResponse response = new LogoutResponse();

        if (token == null || token.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Token non fornito.");
            return response;
        }


        if (!JwtUtil.validateToken(token)) {
            response.setSuccess(false);
            response.setMessage("Token non valido o già scaduto.");
            return response;
        }

        response.setSuccess(true);
        response.setMessage("Logout effettuato con successo.");
        return response;
    }


/**********ELIMINAZIONE ACOUNT********************************************************************************************************/

    @Transactional
    @Override
    public void eliminaAccount(Long idAccount) {
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> new BusinessException("Account non trovato con ID: " + idAccount));

        utenteRepository.findByAccount_Id(idAccount).ifPresent(utenteRepository::delete);
        accountRepository.delete(account);
    }


/**********FUNZIONI AUSILIARE*******************************************************************************************************/

    private Account createAccount(RegisterRequest request, Role ruolo) {
        Account account = new Account();
        account.setNome(request.getNome());
        account.setCognome(request.getCognome());
        account.setMail(request.getMail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setNumero(request.getNumero());
        account.setRuolo(ruolo);
        return account;
    }

    private void createLinkedEntity(Account savedAccount, RegisterRequest request, Role ruolo) {

        switch (ruolo) {
            case UTENTE -> {
                Utente utente = new Utente();
                utente.setAccount(savedAccount);
                utente.setIndirizzo(request.getApprofondimento());
                utente.setDataIscrizione(LocalDateTime.now());
                utenteRepository.save(utente);
            }

            case AGENTE -> {
                Agente agente = new Agente();
                agente.setAccount(savedAccount);
                agente.setAgenzia(request.getApprofondimento());
                agenteRepository.save(agente);
            }

            case ADMIN -> {
            }

            default -> throw new BusinessException("Ruolo non supportato: " + ruolo);
        }
    }

    private RegisterResponse buildRegisterResponse(Account account, RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setIdAccount(account.getId());
        response.setNome(account.getNome());
        response.setCognome(account.getCognome());
        response.setMail(account.getMail());
        response.setNumero(account.getNumero());
        response.setRuolo(account.getRuolo());
        response.setMessaggio(request.getMessaggio() != null ? request.getMessaggio() : "Registrazione completata");
        response.setSuccess(true);

        return response;
    }

    private LoginResponse buildLoginResponse(Account account, String token) {
        LoginResponse response = new LoginResponse();
        response.setMessaggio("Login effettuato con successo.");
        response.setRuolo(account.getRuolo());
        response.setToken(token);
        response.setSuccess(true);
        return response;
    }


    @Override
    public Account getAccountByMail(String mail) {
        return accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new BusinessException("Account non trovato"));
    }



}