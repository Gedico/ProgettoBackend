package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.enums.Categoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InserzioneRepository extends JpaRepository<Inserzione, Long> {

    @Query("""
        SELECT i FROM Inserzione i
        WHERE (:citta IS NULL OR LOWER(i.posizione.descrizione) LIKE LOWER(CONCAT('%', :citta, '%')))
          AND (:categoria IS NULL OR i.categoria = :categoria)
          AND (:prezzoMin IS NULL OR i.prezzo >= :prezzoMin)
          AND (:prezzoMax IS NULL OR i.prezzo <= :prezzoMax)
          AND (:dimensioniMin IS NULL OR i.dimensioni >= :dimensioniMin)
          AND (:dimensioniMax IS NULL OR i.dimensioni <= :dimensioniMax)
    """)
    List<Inserzione> filtra(
            @Param("citta") String citta,
            @Param("categoria") Categoria categoria,
            @Param("prezzoMin") BigDecimal prezzoMin,
            @Param("prezzoMax") BigDecimal prezzoMax,
            @Param("dimensioniMin") Double dimensioniMin,
            @Param("dimensioniMax") Double dimensioniMax
    );

    @Query("SELECT DISTINCT i FROM Inserzione i " +
            "LEFT JOIN FETCH i.posizione " +
            "LEFT JOIN FETCH i.foto")
    List<Inserzione> findAllConRelazioni();

    @Query("""
       SELECT i FROM Inserzione i
       LEFT JOIN FETCH i.foto
       ORDER BY i.dataCreazione DESC
       """)
    List<Inserzione> findUltime4ConFoto(Pageable pageable);

}

