package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Posizione;

import java.util.List;
import java.math.BigDecimal;

@Repository
public interface PosizioneRepository extends JpaRepository<Posizione, Long> {

    //  Trova posizione per latitudine e longitudine esatte
    List<Posizione> findByLatitudineAndLongitudine(BigDecimal latitudine, BigDecimal longitudine);

    //  Trova posizioni che contengono una certa parola nella descrizione (ricerca parziale)
    List<Posizione> findByDescrizioneContainingIgnoreCase(String keyword);

    //  Trova posizione con descrizione esatta (ricerca precisa)
    Posizione findByDescrizione(String descrizione);

    //  Ordina tutte le posizioni in ordine alfabetico per descrizione
    List<Posizione> findAllByOrderByDescrizioneAsc();

    //  Trova posizioni con latitudine e longitudine non nulle
    List<Posizione> findByLatitudineNotNullAndLongitudineNotNull();
 /*
 query importante nel momento in cui operiamo con geopify, che appunto utilizza
 valori come lati e longitudine, e in questo modo andiamo a filtrare e a prendere
 solo i valori corretti, e non nulli
  */

    //  Trova tutte le posizioni in un certo intervallo selezionato (quadrato)
    List<Posizione> findByLatitudineBetweenAndLongitudineBetween(
            BigDecimal minLat, BigDecimal maxLat,
            BigDecimal minLon, BigDecimal maxLon);

    // Ricerca vicina a un punto (mostra quello che c'è nel raggio)
    @Query("SELECT p FROM Posizione p WHERE " +
            "SQRT(POWER(p.latitudine - :lat, 2) + POWER(p.longitudine - :lon, 2)) < :raggio")
    List<Posizione> findByDistanzaDaPunto(
            @Param("lat") BigDecimal lat,
            @Param("lon") BigDecimal lon,
            @Param("raggio") double raggio);


    // Ricerca in base a più parole utilizzate dall'utente per filtrare i risultati
    @Query("SELECT p FROM Posizione p WHERE LOWER(p.descrizione) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Posizione> cercaPerKeyword(@Param("keyword") String keyword);

}
