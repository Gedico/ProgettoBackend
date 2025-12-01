package ProgettoINSW.backend.specification;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.model.Inserzione;
import org.springframework.data.jpa.domain.Specification;

public class InserzioneSpecification {

    public static Specification<Inserzione> filtra(DatiInserzioneFiltriRequest filtri) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            // --- QUERY TESTO LIBERO ---
            if (filtri.getQuery() != null && !filtri.getQuery().trim().isEmpty()) {
                String pattern = "%" + filtri.getQuery().toLowerCase() + "%";

                predicates.getExpressions().add(
                        cb.or(
                                cb.like(cb.lower(root.get("titolo")), pattern),
                                cb.like(cb.lower(root.get("descrizione")), pattern),
                                cb.like(cb.lower(root.get("categoria").as(String.class)), pattern)
                        )
                );
            }

            // --- CITTA ---
            if (filtri.getCitta() != null && !filtri.getCitta().isEmpty()) {
                String pattern = "%" + filtri.getCitta().toLowerCase() + "%";

                predicates.getExpressions().add(
                        cb.like(cb.lower(root.get("posizione").get("descrizione")), pattern)
                );
            }

            // --- CATEGORIA ---
            if (filtri.getCategoria() != null) {
                predicates.getExpressions().add(
                        cb.equal(root.get("categoria"), filtri.getCategoria())
                );
            }

            // --- PREZZI ---
            if (filtri.getPrezzoMin() != null) {
                predicates.getExpressions().add(
                        cb.greaterThanOrEqualTo(root.get("prezzo"), filtri.getPrezzoMin())
                );
            }

            if (filtri.getPrezzoMax() != null) {
                predicates.getExpressions().add(
                        cb.lessThanOrEqualTo(root.get("prezzo"), filtri.getPrezzoMax())
                );
            }

            // --- DIMENSIONI ---
            if (filtri.getDimensioniMin() != null) {
                predicates.getExpressions().add(
                        cb.greaterThanOrEqualTo(root.get("dimensioni"), filtri.getDimensioniMin())
                );
            }

            if (filtri.getDimensioniMax() != null) {
                predicates.getExpressions().add(
                        cb.lessThanOrEqualTo(root.get("dimensioni"), filtri.getDimensioniMax())
                );
            }

            return predicates;
        };
    }
}
