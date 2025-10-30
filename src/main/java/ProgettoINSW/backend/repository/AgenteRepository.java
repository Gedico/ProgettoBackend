package ProgettoINSW.backend.repository;

import ProgettoINSW.backend.model.Agente;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

@Repository
public interface AgenteRepository extends JpaRepository<Agente, Long>{


}

