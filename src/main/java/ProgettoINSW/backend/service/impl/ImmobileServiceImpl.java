package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Immobile;
import ProgettoINSW.backend.model.Posizione;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.service.ImmobileService;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImmobileServiceImpl implements ImmobileService {

    private final InserzioneMap map;
    private final ImmobileRepository immobileRepository;
    private final PosizioneRepository posizioneRepository;
    private final FotoImmobiliRepository fotoRepository;
    private final AgenteRepository agenteRepository;
    private final AccountRepository accountRepository;

    public InserzioneResponse creaInserzione(InserzioneRequest request, String token) {

        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        Posizione posizione = map.toPosizione(request.getPosizione());//riempio dto posizione mediante mapper
        posizioneRepository.save(posizione);

        Immobile immobile = map.toImmobile(request.getImmobile());//riempio dto immobile mediante mapper
        immobile.setPosizione(posizione);
        immobile.setAgente(agente);
        immobileRepository.save(immobile);

        fotoRepository.saveAll(map.toFotoList(request.getFoto(), immobile));

        return map.toInserzioneResponse(immobile);

    }

}
