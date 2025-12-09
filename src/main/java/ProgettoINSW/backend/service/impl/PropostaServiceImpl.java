package ProgettoINSW.backend.service.impl;
import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.dto.proposta.PropostaRequest;
import ProgettoINSW.backend.repository.UtenteRepository;
import java.time.OffsetDateTime;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.service.PropostaService;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
 @RequiredArgsConstructor
public class PropostaServiceImpl implements PropostaService {
    private final PropostaRepository propostaRepository;
    private final AccountRepository accountRepository;
    private final AgenteRepository agenteRepository;
    private final PropostaMap propostaMap;
    private final InserzioneRepository inserzioneRepository;
    private final UtenteRepository utenteRepository;

    @Override
    public List<PropostaResponse> getProposteAgente(String token) {
        return getProposteAgenteByStato(token, null);
    }

    @Override
    public List<PropostaResponse> getProposteAgenteStato(String token, StatoProposta stato) {
        return getProposteAgenteByStato(token, stato);
    }

    @Override
    public PropostaResponse aggiornaStatoProposta(Long id, AggiornaStatoPropostaRequest request, String token) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        Proposta proposta = propostaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposta non trovata con ID: " + id));

        if (!proposta.getInserzione().getAgente().getIdAgente().equals(agente.getIdAgente())) {
            throw new SecurityException("Non sei autorizzato a modificare questa Proposta.");
        }

        StatoProposta nuovoStato = request.getNuovoStato();
        proposta.setStato(nuovoStato);
        propostaRepository.save(proposta);

        if (nuovoStato == StatoProposta.ACCETTATA) {
            Inserzione inserzione = proposta.getInserzione();

            inserzione.setStato(StatoInserzione.VENDUTO);
            inserzioneRepository.save(inserzione);

            List<Proposta> altreProposte = propostaRepository.findAltreProposteByInserzione(inserzione, proposta.getIdProposta());
            for (Proposta altra : altreProposte) {
                if (altra.getStato() == StatoProposta.IN_ATTESA) {
                    altra.setStato(StatoProposta.RIFIUTATA);
                    propostaRepository.save(altra);
                }
            }
        }

        // Se la proposta è RIFIUTATA → non si cambia lo stato dell'inserzione (rimane DISPONIBILE)
        // Non serve fare nulla in questo caso

        return propostaMap.toPropostaResponse(proposta, "Stato proposta aggiornato con successo");

    }

    @Override
    public PropostaResponse mostraDettagliProposta(Long idProposta, String token) {

        Proposta proposta = propostaRepository.findById(idProposta).
                orElseThrow(() -> new EntityNotFoundException("Inserzione con ID " + idProposta + " non trovato."));

        String mailAgente = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mailAgente)
                .orElseThrow(() -> new EntityNotFoundException("account con mail:." + mailAgente+" non esiste. "));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mailAgente));

        if(!proposta.getAgente().getIdAgente().equals(agente.getIdAgente())){
            throw  new RuntimeException("Non puoi visualizzare una proposta che non appartiene alle tue ");
        }


        return propostaMap.toPropostaResponse(proposta,null);
    }

    @Override
    public PropostaResponse inviaProposta(PropostaRequest request, String token) {

        // Recupero mail utente autenticato
        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Utente utente = utenteRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato per l'account: " + mail));

        // Recupero inserzione
        Inserzione inserzione = inserzioneRepository.findById(request.getIdInserzione())
                .orElseThrow(() -> new EntityNotFoundException("Inserzione non trovata con ID: " + request.getIdInserzione()));

        // Controllo disponibilità
        if (inserzione.getStato() != StatoInserzione.DISPONIBILE) {
            throw new RuntimeException("Non è possibile fare proposte su un'inserzione non disponibile.");
        }

        // (Facoltativo) controllo se l'utente ha già inviato una proposta per la stessa inserzione
        boolean esisteGia = propostaRepository.existsByClienteAndInserzione(utente, inserzione);
        if (esisteGia) {
            throw new RuntimeException("Hai già inviato una proposta per questa inserzione.");
        }

        // Creazione proposta
        Proposta proposta = new Proposta();
        proposta.setInserzione(inserzione);
        proposta.setCliente(utente);
        proposta.setAgente(inserzione.getAgente());
        proposta.setPrezzoProposta(request.getPrezzoProposta());
        proposta.setNote(request.getNote());
        proposta.setStato(StatoProposta.IN_ATTESA);
        proposta.setDataProposta(OffsetDateTime.now());

        // Salvataggio nel DB
        propostaRepository.save(proposta);

        // Uso del tuo mapper per creare la risposta
        return propostaMap.toPropostaResponse(proposta, "Proposta inviata con successo.");
    }


    @Override
    public void eliminaProposta(Long idProposta, String token) {

        // 1️⃣ Recupera la mail dal token
        String mail = JwtUtil.extractMail(token);

        // 2️⃣ Trova account e utente associato
        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Utente utente = utenteRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato per l'account: " + mail));

        // 3️⃣ Recupera la proposta da eliminare
        Proposta proposta = propostaRepository.findById(idProposta)
                .orElseThrow(() -> new EntityNotFoundException("Proposta non trovata con ID: " + idProposta));

        // 4️⃣ Verifica che la proposta appartenga all’utente autenticato
        if (!proposta.getCliente().getIdUtente().equals(utente.getIdUtente())) {
            throw new SecurityException("Non sei autorizzato a eliminare questa proposta.");
        }

        // 5️⃣ Può eliminarla solo se è ancora in attesa
        if (proposta.getStato() != StatoProposta.IN_ATTESA) {
            throw new IllegalStateException("Non è possibile eliminare una proposta già accettata o rifiutata.");
        }

        // 6️⃣ Eliminazione dal database
        propostaRepository.delete(proposta);
    }


    //Metodi Utili
/******************************************************************************************************************/

    private List<PropostaResponse> getProposteAgenteByStato(String token, StatoProposta stato) {
        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        List<Proposta> offerte;
        if (stato == null) {
            offerte = propostaRepository.findByAgente(agente);
        } else {
            offerte = propostaRepository.findByAgenteAndStato(agente.getIdAgente(), stato);
        }

        return mapToResponseList(offerte);
    }

    @Override
    public List<PropostaResponse> getProposteAgenteRegistro(String token) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato"));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato"));

        List<Proposta> proposte =
                propostaRepository.findByAgente(agente).stream()
                        .filter(p -> p.getStato() == StatoProposta.ACCETTATA ||
                                p.getStato() == StatoProposta.RIFIUTATA)
                        .toList();

        return mapToResponseList(proposte);
    }


    private List<PropostaResponse> mapToResponseList(List<Proposta> offerte) {
        return offerte.stream()
                .map(o -> {
                    PropostaResponse dto = new PropostaResponse();
                    dto.setIdProposta(o.getIdProposta());
                    dto.setTitoloInserzione(o.getInserzione().getTitolo());
                    dto.setImporto(o.getPrezzoProposta());
                    dto.setStato(o.getStato());
                    dto.setDataCreazione(o.getDataProposta());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<PropostaResponse> getProposteUtente(String token) {

        // 1) recupera mail dal JWT
        String mail = JwtUtil.extractMail(token);

        // 2) recupera account dalla mail
        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per mail: " + mail));

        // 3) recupera utente legato all'account
        Utente utente = utenteRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato per account: " + account.getId()));

        // 4) recupera tutte le proposte fatte da quell’utente
        List<Proposta> proposte = propostaRepository.findByCliente_IdUtente(utente.getIdUtente());

        // 5) mappa in response
        return proposte.stream()
                .map(p -> propostaMap.toPropostaResponse(p, null))
                .toList();
    }


}
