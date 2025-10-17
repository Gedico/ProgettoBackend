package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UtenteRepository extends JpaRepository<Utente,Long>{

    boolean existsByEmail(String email);

    Utente save(Utente utente);

}
