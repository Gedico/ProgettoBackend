package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.proposta.*;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.model.enums.TipoProponente;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.service.PropostaService;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropostaServiceImpl implements PropostaService {

    private final PropostaRepository propostaRepository;
    private final AccountRepository accountRepository;
    private final AgenteRepository agenteRepository;
    private final UtenteRepository utenteRepository;
    private final InserzioneRepository inserzioneRepository;
    private final PropostaMap propostaMap;

    /* =========================
       METODI PUBBLICI
       ========================= */

    @Override
    public List<PropostaResponse> getProposteAgente(String token) {
        return getProposteAgenteByStato(token, null);
    }

    @Override
    public List<PropostaResponse> getProposteAgenteStato(String token, StatoProposta stato) {
        return getProposteAgenteByStato(token, stato);
    }

    @Override
    public PropostaResponse aggiornaStatoProposta(
            Long id,
            AggiornaStatoPropostaRequest request,
            String token) {

        Agente agente = getAgenteFromToken(token);
        Proposta proposta = getPropostaOrThrow(id);

        verificaAgenteAutorizzato(proposta, agente);

        proposta.setStato(request.getNuovoStato());
        propostaRepository.save(proposta);

        if (request.getNuovoStato() == StatoProposta.ACCETTATA) {
            gestisciAccettazioneProposta(proposta);
        }

        return propostaMap.toPropostaResponse(
                proposta,
                "Stato proposta aggiornato con successo"
        );
    }

    @Override
    public PropostaResponse creaControproposta(
            Long idProposta,
            ContropropostaRequest request,
            String token) {


        Agente agente = getAgenteFromToken(token);
        Proposta propostaOriginale = getPropostaOrThrow(idProposta);

        verificaAgenteAutorizzato(propostaOriginale, agente);
        verificaPropostaModificabile(propostaOriginale);

        validaPrezzoProposta(
                request.getNuovoPrezzo(),
                propostaOriginale.getInserzione()
        );

        propostaOriginale.setStato(StatoProposta.RIFIUTATA);
        propostaRepository.save(propostaOriginale);

        Proposta controproposta = creaNuovaControproposta(propostaOriginale, agente, request);
        propostaRepository.save(controproposta);

        return propostaMap.toPropostaResponse(
                controproposta,
                "Controproposta inviata con successo"
        );
    }

    @Override
    public PropostaResponse inviaProposta(PropostaRequest request, String token) {

        Utente utente = getUtenteFromToken(token);
        Inserzione inserzione = getInserzioneDisponibile(request.getIdInserzione());

        validaPrezzoProposta(request.getPrezzoProposta(), inserzione);

        if (propostaRepository.existsByClienteAndInserzione(utente, inserzione)) {
            throw new IllegalStateException("Hai già inviato una proposta per questa inserzione");
        }

        Proposta proposta = new Proposta();
        proposta.setInserzione(inserzione);
        proposta.setCliente(utente);
        proposta.setAgente(inserzione.getAgente());
        proposta.setPrezzoProposta(request.getPrezzoProposta());
        proposta.setNote(request.getNote());
        proposta.setStato(StatoProposta.IN_ATTESA);
        proposta.setProponente(TipoProponente.UTENTE);
        proposta.setPropostaPrecedente(null);
        proposta.setDataProposta(OffsetDateTime.now());

        propostaRepository.save(proposta);

        return propostaMap.toPropostaResponse(
                proposta,
                "Proposta inviata con successo"
        );
    }

    @Override
    public void eliminaProposta(Long idProposta, String token) {

        Utente utente = getUtenteFromToken(token);
        Proposta proposta = getPropostaOrThrow(idProposta);

        if (!proposta.getCliente().getIdUtente().equals(utente.getIdUtente())) {
            throw new AccessDeniedException("Non sei autorizzato a eliminare questa proposta");
        }

        verificaPropostaModificabile(proposta);
        propostaRepository.delete(proposta);
    }

    @Override
    public PropostaResponse mostraDettagliProposta(Long idProposta, String token) {

        Agente agente = getAgenteFromToken(token);
        Proposta proposta = getPropostaOrThrow(idProposta);

        verificaAgenteAutorizzato(proposta, agente);

        return propostaMap.toPropostaResponse(proposta, null);
    }

    @Override
    public List<PropostaResponse> getProposteAgenteRegistro(String token) {

        Agente agente = getAgenteFromToken(token);

        return propostaRepository.findByAgente(agente).stream()
                .filter(p -> p.getStato() == StatoProposta.ACCETTATA
                        || p.getStato() == StatoProposta.RIFIUTATA)
                .map(p -> propostaMap.toPropostaResponse(p, null))
                .toList();
    }

    @Override
    public List<PropostaResponse> getProposteUtente(String token) {

        Utente utente = getUtenteFromToken(token);

        return propostaRepository.findByCliente_IdUtente(utente.getIdUtente())
                .stream()
                .map(p -> propostaMap.toPropostaResponse(p, null))
                .toList();
    }

    /* =========================
       METODI PRIVATI
       ========================= */

    private Agente getAgenteFromToken(String token) {
        Account account = getAccountFromToken(token);
        return agenteRepository.findByAccount(account)
                .orElseThrow(() ->
                        new EntityNotFoundException("Agente non trovato per account"));
    }

    private Utente getUtenteFromToken(String token) {
        Account account = getAccountFromToken(token);
        return utenteRepository.findByAccount_Id(account.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Utente non trovato per account"));
    }

    private Account getAccountFromToken(String token) {
        String mail = JwtUtil.extractMail(token);
        return accountRepository.findByMail(mail)
                .orElseThrow(() ->
                        new EntityNotFoundException("Account non trovato per mail: " + mail));
    }

    private Proposta getPropostaOrThrow(Long id) {
        return propostaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Proposta non trovata con ID: " + id));
    }

    private List<PropostaResponse> getProposteAgenteByStato(String token, StatoProposta stato) {
        Agente agente = getAgenteFromToken(token);

        List<Proposta> offerte;
        if (stato == null) {
            offerte = propostaRepository.findByAgente(agente);
        } else {
            offerte = propostaRepository.findByAgenteAndStato(agente.getIdAgente(), stato);
        }

        return offerte.stream()
                .map(p -> propostaMap.toPropostaResponse(p, null))
                .toList();
    }


    private Inserzione getInserzioneDisponibile(Long idInserzione) {
        Inserzione inserzione = inserzioneRepository.findById(idInserzione)
                .orElseThrow(() ->
                        new EntityNotFoundException("Inserzione non trovata"));

        if (inserzione.getStato() != StatoInserzione.DISPONIBILE) {
            throw new IllegalStateException("Inserzione non disponibile");
        }
        return inserzione;
    }

    private void verificaAgenteAutorizzato(Proposta proposta, Agente agente) {
        if (!proposta.getAgente().getIdAgente().equals(agente.getIdAgente())) {
            throw new AccessDeniedException("Operazione non autorizzata");
        }
    }

    private void verificaPropostaModificabile(Proposta proposta) {
        if (proposta.getStato() == StatoProposta.ACCETTATA || proposta.getStato() == StatoProposta.RIFIUTATA) {
            throw new IllegalStateException("La proposta non è modificabile");
        }
    }

    private void gestisciAccettazioneProposta(Proposta proposta) {
        Inserzione inserzione = proposta.getInserzione();
        inserzione.setStato(StatoInserzione.VENDUTO);
        inserzioneRepository.save(inserzione);

        propostaRepository
                .findAltreProposteByInserzione(inserzione, proposta.getIdProposta())
                .forEach(p -> {
                    if (p.getStato() == StatoProposta.IN_ATTESA) {
                        p.setStato(StatoProposta.RIFIUTATA);
                        propostaRepository.save(p);
                    }
                });
    }

    private Proposta creaNuovaControproposta(
            Proposta originale,
            Agente agente,
            ContropropostaRequest request) {

        Proposta p = new Proposta();
        p.setInserzione(originale.getInserzione());
        p.setCliente(originale.getCliente());
        p.setAgente(agente);
        p.setPrezzoProposta(request.getNuovoPrezzo());
        p.setNote(request.getNote());

        p.setStato(StatoProposta.CONTROPROPOSTA);
        p.setProponente(TipoProponente.AGENTE);
        p.setPropostaPrecedente(originale);

        p.setDataProposta(OffsetDateTime.now());
        return p;
    }

    private void validaPrezzoProposta(BigDecimal prezzoProposto, Inserzione inserzione) {

        if (prezzoProposto == null) {
            throw new IllegalArgumentException("Il prezzo non può essere nullo");
        }

        if (prezzoProposto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero");
        }

        BigDecimal prezzoInserzione = inserzione.getPrezzo();

        // minimo accettato = prezzo - 15%
        BigDecimal minimoAccettato = prezzoInserzione
                .multiply(new BigDecimal("0.85"))
                .setScale(2, RoundingMode.HALF_UP);

        if (prezzoProposto.compareTo(minimoAccettato) < 0) {
            throw new IllegalArgumentException(
                    "L'importo offerto è inferiore al minimo accettato (" +
                            minimoAccettato + " €)"
            );
        }
    }

}
