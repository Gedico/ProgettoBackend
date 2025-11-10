package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long>{


    List<Foto> findByInserzioneAndUrlFotoIn(Inserzione inserzione, List<String> urlDaEliminare);
}
