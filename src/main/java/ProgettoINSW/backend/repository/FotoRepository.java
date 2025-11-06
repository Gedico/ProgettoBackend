package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long>{



}
