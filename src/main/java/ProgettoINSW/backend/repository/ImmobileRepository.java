package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Immobile;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ImmobileRepository extends JpaRepository<Immobile, Long> {

    // Immobili gestiti da un determinato agente
    List<Immobile> findByAgenteIdAgente(Long idAgente);

    // Immobili in una determinata posizione
    List<Immobile> findByPosizioneIdPosizione(Long idPosizione);

    // Immobili filtrati per categoria
    List<Immobile> findByCategoria(String categoria);

    // Filtraggio in base al prezzo
    List<Immobile> findByPrezzo(BigDecimal prezzo);

    // Filtraggio in base a range di prezzo scelto
    List<Immobile> findByPrezzoBetween(BigDecimal min, BigDecimal max);

    // Filtraggio in base alla presenza di ascensore
    List<Immobile> findByAscensoreTrue();

    // Filtraggio in base a numero di stanze
    List<Immobile> findByNumeroStanzeGreaterThanEqual(Integer minStanze);

    // Filtraggio in base a una determinata metratura
    List<Immobile> findByDimensioniBetween(Integer minMq, Integer maxMq);

    // Immobili in una posizione + categoria
    List<Immobile> findByPosizioneIdPosizioneAndCategoria(Long idPosizione, String categoria);

    // Immobili inn una posizione + categoria + prezzo
    List<Immobile> findByPosizioneIdPosizioneAndCategoriaAndPrezzoBetween(Long idPosizione, String categoria, BigDecimal min, BigDecimal max);

    // Ordinamento per prezzo crescente o decrescente
    List<Immobile> findAllByOrderByPrezzoAsc();
    List<Immobile> findAllByOrderByPrezzoDesc();

    // Ordinamento per data di creazione (più recenti prima)
    List<Immobile> findAllByOrderByDataCreazioneDesc();

    // Ordinamento per data di creazione (meno recente prima)
    List<Immobile> findAllByOrderByDataCreazioneAsc();

    // Ricerca in base a titolo o descrizione
    List<Immobile> findByTitoloContainingIgnoreCaseOrDescrizioneContainingIgnoreCase(String titolo, String descrizione);

    // Ricerca di immobile entro una certa area geografica
    @Query("""
       SELECT i FROM Immobile i
       WHERE i.posizione.latitudine BETWEEN :minLat AND :maxLat
         AND i.posizione.longitudine BETWEEN :minLon AND :maxLon
       """)
    List<Immobile> findImmobiliInArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );
 /*
  Adesso ho dovuto scrivere una query personalizzata perchè non vi era possibilità di utilizzare le funzionalità
  disposte da Spring. Tale query seleziona gli immobili in cui la latitudine e longitudine rientrano nel rettangolo
  selezionato dalla mappa Geopify
  */

    @Query("SELECT COUNT(i) FROM Immobile i WHERE i.agente.idAgente = :idAgente")
    long countByAgente(@Param("idAgente") Long idAgente);
  /*
  Questa query mi serve per numerare gli immobili gestiti da un agente, cosi posso avere un resoconto
  personale del numero di immobili che sono associati ad un determinato agente, e di cui, quindi, si occupa
   */

}

