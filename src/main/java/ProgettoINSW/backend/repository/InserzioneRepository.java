package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Inserzione;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InserzioneRepository
        extends JpaRepository<Inserzione, Long>,
        JpaSpecificationExecutor<Inserzione> {

    // === NON collegato alla searchbar ===

    @Query("""
        SELECT DISTINCT i FROM Inserzione i
        LEFT JOIN FETCH i.posizione
        LEFT JOIN FETCH i.foto
    """)
    List<Inserzione> findAllConRelazioni();

    @Query("""
        SELECT i FROM Inserzione i
        LEFT JOIN FETCH i.foto
        ORDER BY i.dataCreazione DESC
    """)
    List<Inserzione> findUltime4ConFoto(Pageable pageable);

    List<Inserzione> findByAgente_IdAgente(Long idAgente);
}

