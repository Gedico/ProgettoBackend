package ProgettoINSW.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ProgettoINSW.backend.model.Offerta;


import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OffertaRepository extends JpaRepository<Offerta, Long> {

    List<Offerta> findByidImmobile(Long idImmobile);

    List<Offerta> findByClienteIdUtente(Long idUtente);

    List<Offerta> findByAgenteIdAgente(Long idAgente);

    // Offerte con stato specifico
    List<Offerta> findByStatoOfferta(String stato);

   //E POI, chiamo o in controller o in service:
    //List<Offerta> offerteInAttesa = offertaRepository.findByStatoOfferta("in_attesa");
    //quindi praticamente qui mi limito a scriverlo in modo generale

    // Offerte ordinate per data (più recenti prima)
    List<Offerta> findAllByOrderByDataOffertaDesc();

    //offerte ordinate per data (meno recenti prima)
    List<Offerta> findAllByOrderByDataOffertaAsc();

    // Offerte di un certo immobile fatte da un cliente specifico
    List<Offerta> findByImmobileIdImmobileAndClienteIdUtente(Long idImmobile, Long idUtente);

    // Offerte di un agente in uno stato specifico
    List<Offerta> findByAgenteIdAgenteAndStatoOfferta(Long idAgente, String stato);

    //Offerte ordinate per prezzo decrescente
    List<Offerta> findByImmobileIdImmobileOrderByDataOffertaDesc(Long idImmobile);

    //Offerte di un cliente ordinate per prezzo decrescente (piu costose prima)
    List<Offerta> findByClienteIdUtenteOrderByPrezzoOffertaDesc(Long idUtente);

    List<Offerta> findByClienteIdUtenteOrderByPrezzoOffertaAsc(Long idUtente);

    List<Offerta> findByPrezzoOffertaBetween(BigDecimal minPrezzo, BigDecimal maxPrezzo);


    // Conta quante offerte ci sono per un certo immobile
    int countByImmobileIdImmobile(Long idImmobile);

    // Verifica se esiste un’offerta di un certo cliente su un immobile
    boolean existsByImmobileIdImmobileAndClienteIdUtente(Long idImmobile, Long idUtente);


}
