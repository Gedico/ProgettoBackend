package ProgettoINSW.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ProgettoINSW.backend.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    boolean existsByMail(String mail);

    boolean existsByMailIgnoreCase(String mail);


}
