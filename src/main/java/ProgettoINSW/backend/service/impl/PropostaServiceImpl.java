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
    public List<PropostaResponse> getOfferteAgente(String token, StatoProposta stato) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        List<Proposta> offerte = propostaRepository.findByAgenteAndStato(agente.getIdAgente(), stato);

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
    public PropostaResponse aggiornaStatoProposta(Long id, AggiornaStatoPropostaRequest request, String token) {

        String mail = JwtUtil.extractMail(token);

        // 1️⃣ Recupera l'agente autenticato
        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        // 2️⃣ Recupera la Proposta
        Proposta proposta = propostaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposta non trovata con ID: " + id));

        // 3️⃣ Verifica che l'agente sia proprietario dell'inserzione associato
        if (!proposta.getInserzione().getAgente().getIdAgente().equals(agente.getIdAgente())) {
            throw new SecurityException("Non sei autorizzato a modificare questa Proposta.");
        }

        // 4️⃣ Aggiorna lo stato
        proposta.setStato(request.getNuovoStato());
        propostaRepository.save(proposta);

        // Se la Proposta è ACCETTATA → segna l'inserzione come VENDUTO (quando è in vendita)
        if (request.getNuovoStato() == StatoProposta.ACCETTATA) {
            Inserzione inserzione = proposta.getInserzione();
            inserzione.setStato(StatoInserzione.VENDUTO);
            inserzioneRepository.save(inserzione);
        }

        // 5️⃣ Mapper → DTO
        return propostaMap.toDto(proposta, "Stato Proposta aggiornato con successo");
    }

}
