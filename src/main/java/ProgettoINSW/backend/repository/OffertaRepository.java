package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.enums.StatoOfferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Offerta;


import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OffertaRepository extends JpaRepository<Offerta, Long> {

    @Query("""
        SELECT o FROM Offerta o
        WHERE o.immobile.agente.idAgente = :idAgente
          AND (:stato IS NULL OR o.stato = :stato)
    """)
    List<Offerta> findByAgenteAndStato(
            @Param("idAgente") Long idAgente,
            @Param("stato") StatoOfferta stato
    );


}
