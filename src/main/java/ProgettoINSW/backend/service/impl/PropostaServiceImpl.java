package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Proposta;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.AgenteRepository;
import ProgettoINSW.backend.repository.PropostaRepository;
import ProgettoINSW.backend.repository.InserzioneRepository;
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

        proposta.setStato(request.getNuovoStato());
        propostaRepository.save(proposta);

        if (request.getNuovoStato() == StatoProposta.ACCETTATA) {
            Inserzione inserzione = proposta.getInserzione();
            inserzione.setStato(StatoInserzione.VENDUTO);
            inserzioneRepository.save(inserzione);
        }

        return propostaMap.toPropostaResponse(proposta, "Stato Proposta aggiornato con successo");
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

}
