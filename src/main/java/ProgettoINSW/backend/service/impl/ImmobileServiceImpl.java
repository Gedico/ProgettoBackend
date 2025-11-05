package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.immobile.ImmobileFiltriRequest;
import ProgettoINSW.backend.dto.immobile.ModificaImmobileRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.Categoria;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.model.enums.StatoOfferta;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.service.ImmobileService;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImmobileServiceImpl implements ImmobileService {

    private final InserzioneMap map;
    private final ImmobileRepository immobileRepository;
    private final PosizioneRepository posizioneRepository;
    private final FotoImmobiliRepository fotoRepository;
    private final AgenteRepository agenteRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @Override
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

    @Transactional
    @Override
    public ModificaImmobileRequest modificaInserzione(Long idImmobile, ModificaImmobileRequest request, String token) {

        String mailAgente = JwtUtil.extractMail(token);

        Immobile immobile = immobileRepository.findById(idImmobile)
                .orElseThrow(() -> new RuntimeException("Immobile non trovato"));

        Account accountRichiedente = accountRepository.findByMailIgnoreCase(mailAgente)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = immobile.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi modificare un'immobile che non hai pubblicato");
        }

        if (request.getTitolo() != null) immobile.setTitolo(request.getTitolo());
        if (request.getDescrizione() != null) immobile.setDescrizione(request.getDescrizione());
        if (request.getPrezzo() != null) immobile.setPrezzo(request.getPrezzo());
        if (request.getDimensioni() != null) immobile.setDimensioni(request.getDimensioni());
        if (request.getNumeroStanze() != null) immobile.setNumeroStanze(request.getNumeroStanze());
        if (request.getPiano() != null) immobile.setPiano(request.getPiano());
        if (request.getAscensore() != null) immobile.setAscensore(request.getAscensore());
        if (request.getClasseEnergetica() != null) immobile.setClasseEnergetica(request.getClasseEnergetica());
        if (request.getCategoria() != null)
            immobile.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));

        immobileRepository.save(immobile);

        return new ModificaImmobileRequest("Immobile aggiornato con successo", true);
    }

    @Override
    public List<InserzioneResponse> ricercaImmobili(ImmobileFiltriRequest filtri) {

        List<Immobile> risultati = immobileRepository.filtra(
                filtri.getCitta(),
                filtri.getCategoria(),
                filtri.getPrezzoMin(),
                filtri.getPrezzoMax(),
                filtri.getDimensioniMin(),
                filtri.getDimensioniMax()
        );

        return risultati.stream()
                .map(map::toInserzioneResponse)
                .toList();
    }

    @Override
    public InserzioneResponse getInserzioneById(Long id) {
        Immobile immobile = immobileRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Immobile con ID " + id + " non trovato."));
        return map.toInserzioneResponse(immobile);
    }

    @Override
    public void eliminaInserzione(Long id, String token) {
        String mailAgente = JwtUtil.extractMail(token);

        Immobile immobile = immobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobile non trovato"));

        Account accountRichiedente = accountRepository.findByMailIgnoreCase(mailAgente)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = immobile.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi eliminare un'immobile che non hai pubblicato");
        }

        immobileRepository.delete(immobile);
    }

    @Transactional
    @Override
    public void caricaFotoImmobile(Long id, String token,List<FotoRequest> nuoveFoto) {
        String mailAgente = JwtUtil.extractMail(token);

        Immobile immobile = immobileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immobile non trovato"));

        Account accountRichiedente = accountRepository.findByMailIgnoreCase(mailAgente)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = immobile.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi caricare foto per un'immobile che non hai pubblicato");
        }

        // --- 🔹 Logica base di aggiunta foto ---
        if (nuoveFoto == null || nuoveFoto.isEmpty()) {
            throw new RuntimeException("Nessuna foto fornita");
        }

        List<FotoImmobili> fotoDaSalvare = nuoveFoto.stream()
                .map(f -> {
                    FotoImmobili foto = new FotoImmobili();
                    foto.setUrlFoto(f.getUrl());
                    foto.setImmobile(immobile);
                    return foto;
                })
                .collect(Collectors.toList());

        fotoRepository.saveAll(fotoDaSalvare);



    }


}
