package ProgettoINSW.backend.specification;

import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class InserzioneSpecification {

    public static Specification<Inserzione> filtra(InserzioneSearchRequest filtri) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // === COMUNE (OBBLIGATORIO) ===
            predicates.add(
                    cb.equal(
                            cb.lower(root.get("posizione").get("comune")),
                            filtri.getComune().toLowerCase()
                    )
            );

            // === CATEGORIA ===
            if (filtri.getCategoria() != null) {
                predicates.add(
                        cb.equal(root.get("categoria"), filtri.getCategoria())
                );
            }

            // === PREZZO ===
            if (filtri.getPrezzoMin() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("prezzo"), filtri.getPrezzoMin())
                );
            }

            if (filtri.getPrezzoMax() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("prezzo"), filtri.getPrezzoMax())
                );
            }

            // === DIMENSIONI ===
            if (filtri.getDimensioniMin() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("dimensioni"), filtri.getDimensioniMin())
                );
            }

            if (filtri.getDimensioniMax() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("dimensioni"), filtri.getDimensioniMax())
                );
            }

            // === NUMERO STANZE ===
            if (filtri.getNumeroStanze() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("numeroStanze"), filtri.getNumeroStanze())
                );
            }

            // === ASCENSORE ===
            if (filtri.getAscensore() != null) {
                predicates.add(
                        cb.equal(root.get("ascensore"), filtri.getAscensore())
                );
            }

            // === STATO INSERZIONE ===
            if (filtri.getStato() != null) {
                predicates.add(
                        cb.equal(root.get("stato"), filtri.getStato())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}



