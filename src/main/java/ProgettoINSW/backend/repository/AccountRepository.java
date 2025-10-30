package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByMailIgnoreCase(String mail); //serve per la registrazione, per verificare se esiste già l'email
    Optional<Account> findByMailIgnoreCase(String mail); //serve per cercare l'utente e accedere ai dati

    Optional<Account> findByMail(String mail);



}
