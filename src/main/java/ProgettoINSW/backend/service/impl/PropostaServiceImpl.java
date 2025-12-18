package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.proposta.*;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.model.enums.TipoProponente;
import ProgettoINSW.backend.model.enums.TipoProposta;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.service.PropostaService;
import ProgettoINSW.backend.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        proposta.setProponente(TipoProponente.UTENTE);
        proposta.setTipo(TipoProposta.ONLINE); // 🔥 FIX CRITICO
        proposta.setStato(StatoProposta.IN_ATTESA);

        proposta.setPropostaPrecedente(null);
        proposta.setDataProposta(OffsetDateTime.now());

        propostaRepository.save(proposta);

        return propostaMap.toPropostaResponse(
                proposta,
                "Proposta inviata con successo"
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

        Proposta controproposta =
                creaNuovaControproposta(propostaOriginale, agente, request);

        propostaRepository.save(controproposta);

        return propostaMap.toPropostaResponse(
                controproposta,
                "Controproposta inviata con successo"
        );
    }

    @Override
    public PropostaResponse creaPropostaManuale(
            Long idInserzione,
            PropostaManualeRequest request,
            String token) {

        Agente agente = getAgenteFromToken(token);
        Inserzione inserzione = getInserzioneDisponibile(idInserzione);

        validaPrezzoProposta(request.getPrezzoProposta(), inserzione);

        Proposta proposta = new Proposta();
        proposta.setInserzione(inserzione);
        proposta.setAgente(agente);
        proposta.setCliente(null);

        proposta.setNomeCliente(request.getNomeCliente());
        proposta.setContattoCliente(request.getContattoCliente());
        proposta.setPrezzoProposta(request.getPrezzoProposta());
        proposta.setNote(request.getNote());

        proposta.setProponente(TipoProponente.AGENTE);
        proposta.setTipo(TipoProposta.MANUALE);
        proposta.setStato(StatoProposta.IN_ATTESA);

        proposta.setPropostaPrecedente(null);
        proposta.setDataProposta(OffsetDateTime.now());

        propostaRepository.save(proposta);

        return propostaMap.toPropostaResponse(
                proposta,
                "Proposta manuale inserita con successo"
        );
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
                .filter(p ->
                        p.getStato() == StatoProposta.ACCETTATA ||
                                p.getStato() == StatoProposta.RIFIUTATA)
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

        p.setProponente(TipoProponente.AGENTE);
        p.setTipo(TipoProposta.ONLINE); // 🔥 FIX
        p.setStato(StatoProposta.CONTROPROPOSTA);

        p.setPropostaPrecedente(originale);
        p.setDataProposta(OffsetDateTime.now());

        return p;
    }

    private void validaPrezzoProposta(BigDecimal prezzoProposto, Inserzione inserzione) {

        if (prezzoProposto == null || prezzoProposto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prezzo non valido");
        }

        BigDecimal minimoAccettato = inserzione.getPrezzo()
                .multiply(new BigDecimal("0.85"))
                .setScale(2, RoundingMode.HALF_UP);

        if (prezzoProposto.compareTo(minimoAccettato) < 0) {
            throw new IllegalArgumentException(
                    "Importo inferiore al minimo accettato (" + minimoAccettato + " €)"
            );
        }
    }

    private Inserzione getInserzioneDisponibile(Long idInserzione) {
        Inserzione inserzione = inserzioneRepository.findById(idInserzione)
                .orElseThrow(() -> new EntityNotFoundException("Inserzione non trovata"));

        if (inserzione.getStato() != StatoInserzione.DISPONIBILE) {
            throw new IllegalStateException("Inserzione non disponibile");
        }
        return inserzione;
    }

    private Proposta getPropostaOrThrow(Long id) {
        return propostaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Proposta non trovata con ID: " + id));
    }

    private Agente getAgenteFromToken(String token) {
        Account account = getAccountFromToken(token);
        return agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato"));
    }

    private Utente getUtenteFromToken(String token) {
        Account account = getAccountFromToken(token);
        return utenteRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));
    }

    private Account getAccountFromToken(String token) {
        String mail = JwtUtil.extractMail(token);
        return accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato"));
    }

    private List<PropostaResponse> getProposteAgenteByStato(
            String token,
            StatoProposta stato) {

        Agente agente = getAgenteFromToken(token);

        List<Proposta> proposte =
                stato == null
                        ? propostaRepository.findByAgente(agente)
                        : propostaRepository.findByAgenteAndStato(
                        agente.getIdAgente(), stato);

        return proposte.stream()
                .map(p -> propostaMap.toPropostaResponse(p, null))
                .toList();
    }

    private void verificaAgenteAutorizzato(Proposta proposta, Agente agente) {
        if (!proposta.getAgente().getIdAgente().equals(agente.getIdAgente())) {
            throw new AccessDeniedException("Operazione non autorizzata");
        }
    }

    private void verificaPropostaModificabile(Proposta proposta) {
        if (proposta.getStato() == StatoProposta.ACCETTATA ||
                proposta.getStato() == StatoProposta.RIFIUTATA) {
            throw new IllegalStateException("La proposta non è modificabile");
        }
    }

    private void gestisciAccettazioneProposta(Proposta proposta) {
        Inserzione inserzione = proposta.getInserzione();
        inserzione.setStato(StatoInserzione.VENDUTO);
        inserzioneRepository.save(inserzione);
    }
}
