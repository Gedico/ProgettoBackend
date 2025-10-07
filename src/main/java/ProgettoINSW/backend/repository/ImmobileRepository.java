package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Immobile;

@Repository
public interface ImmobileRepository extends JpaRepository<Immobile, Integer> {
}
