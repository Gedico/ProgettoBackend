package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente,Long>{

    // Trova l’utente collegato a un account tramite id_account
    Optional<Utente> findByAccount_Id(Long idAccount);




}
