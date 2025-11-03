package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.offerta.AggiornaStatoOffertaRequest;
import ProgettoINSW.backend.dto.offerta.OffertaResponse;
import ProgettoINSW.backend.mapper.OffertaMap;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Offerta;
import ProgettoINSW.backend.model.enums.StatoOfferta;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.AgenteRepository;
import ProgettoINSW.backend.repository.OffertaRepository;
import ProgettoINSW.backend.service.OffertaService;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
 @RequiredArgsConstructor
public class OffertaServiceImpl implements OffertaService {
    private final OffertaRepository offertaRepository;
    private final AccountRepository accountRepository;
    private final AgenteRepository agenteRepository;
    private final OffertaMap offertaMap;

    @Override
    public List<OffertaResponse> getOfferteAgente(String token, StatoOfferta stato) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        List<Offerta> offerte = offertaRepository.findByAgenteAndStato(agente.getIdAgente(), stato);

        return offerte.stream()
                .map(o -> {
                    OffertaResponse dto = new OffertaResponse();
                    dto.setIdOfferta(o.getIdOfferta());
                    dto.setTitoloImmobile(o.getImmobile().getTitolo());
                    dto.setImporto(o.getPrezzoOfferta());
                    dto.setStato(o.getStato());
                    dto.setDataCreazione(o.getDataOfferta());
                    return dto;
                })
                .toList();
    }


    @Override
    public OffertaResponse aggiornaStatoOfferta(Long idOfferta, AggiornaStatoOffertaRequest request, String token) {

        String mail = JwtUtil.extractMail(token);

        // 1️⃣ Recupera l'agente autenticato
        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        // 2️⃣ Recupera l'offerta
        Offerta offerta = offertaRepository.findById(idOfferta)
                .orElseThrow(() -> new EntityNotFoundException("Offerta non trovata con ID: " + idOfferta));

        // 3️⃣ Verifica che l'agente sia proprietario dell'immobile associato
        if (!offerta.getImmobile().getAgente().getIdAgente().equals(agente.getIdAgente())) {
            throw new SecurityException("Non sei autorizzato a modificare questa offerta.");
        }

        // 4️⃣ Aggiorna lo stato
        offerta.setStato(request.getNuovoStato());
        offertaRepository.save(offerta);

        // 5️⃣ Mapper → DTO
        return offertaMap.toDto(offerta, "Stato offerta aggiornato con successo");
    }

}
