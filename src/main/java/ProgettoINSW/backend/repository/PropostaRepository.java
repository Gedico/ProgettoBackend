package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.enums.StatoProposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Proposta;
import ProgettoINSW.backend.model.Agente;


import java.util.List;

@Repository
public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    @Query("""
        SELECT o FROM Proposta o
        WHERE o.inserzione.agente.idAgente = :idAgente
          AND (:stato IS NULL OR o.stato = :stato)
    """)
    List<Proposta> findByAgenteAndStato(
            @Param("idAgente") Long idAgente,
            @Param("stato") StatoProposta stato
    );


    List<Proposta> findByAgente(Agente agente);

}
