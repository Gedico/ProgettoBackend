package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.IndicatoreProssimita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicatoreProssimitaRepository extends JpaRepository<IndicatoreProssimita, Long> {

    IndicatoreProssimita findByInserzione_IdInserzione(Long idInserzione);

    boolean existsByInserzione_IdInserzione(Long idInserzione);
}

