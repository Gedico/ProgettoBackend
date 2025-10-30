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
public interface FotoImmobiliRepository extends JpaRepository<FotoImmobili, Long>{



}
