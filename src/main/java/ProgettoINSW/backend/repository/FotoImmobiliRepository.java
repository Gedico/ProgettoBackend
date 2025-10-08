package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.FotoImmobili;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.FotoImmobili;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FotoImmobiliRepository {

    // Recupera tutte le foto di un immobile
    List<FotoImmobili> findByImmobileId(Long IdImmobile);

    // Trova foto tramite url
    Optional<FotoImmobili> findByUrlFoto(String urlFoto);

    // Cancella tutte le foto di un immobile
    @Transactional
    void deleteByImmobileId(Long idImmobile);

    // Conteggio delle foto di un determinato immobile
    long countByImmobileId(Long idImmobile);

    // Cerca foto utilizzando una parola chiave
    List<FotoImmobili> findByUrlFotoContainingIgnoreCase(String keyword);
 /*
 Ad esempio, se le raccogliamo attraverso tag, le possiamo subito trovare
  */

}
