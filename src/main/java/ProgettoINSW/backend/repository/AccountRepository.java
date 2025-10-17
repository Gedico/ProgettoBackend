package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMailIgnoreCase(String mail);

    Account save(Account  account);



}
