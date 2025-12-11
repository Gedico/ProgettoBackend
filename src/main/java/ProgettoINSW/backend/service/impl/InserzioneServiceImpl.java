package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.Categoria;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.service.*;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.specification.InserzioneSpecification;
import ProgettoINSW.backend.util.JwtUtil;
import ProgettoINSW.backend.util.ValidazioneUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InserzioneServiceImpl implements InserzioneService {


    private final InserzioneMap map;
    private final InserzioneRepository inserzioneRepository;
    private final ValidazioneUtil validazioneUtil;
    private final IndicatoreProssimitaService indicatoreProssimitaService;
    private final FotoService fotoService;
    private final AgenteService agenteService;
    private final PosizioneService posizioneService;


    /*********CREA INSERZIONE*************************************************************************************************************/

    @Transactional
    @Override
    public InserzioneResponse creaInserzione(InserzioneRequest request,
                                             MultipartFile[] immagini,
                                             String token) throws IOException {


        Agente agente = agenteService.getAgenteFromToken(token);

        Posizione posizione = posizioneService.creaPosizione(request.getPosizione());

        Inserzione inserzione = map.toEntity(request, posizione, agente);
        inserzioneRepository.save(inserzione);

        fotoService.processImages(immagini, inserzione);

        indicatoreProssimitaService.generaIndicatoriPerInserzione(inserzione);

        return map.toInserzioneResponse(inserzione);
    }




    /******MODIFICA INSERZIONE*************************************************************************************************************************/

    @Transactional
    @Override
    public DatiInserzioneRequest modificaInserzione(Long id, DatiInserzioneRequest request, String token) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi modificare un'inserzione che non hai pubblicato");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        if (request.getTitolo() != null) inserzione.setTitolo(request.getTitolo());
        if (request.getDescrizione() != null) inserzione.setDescrizione(request.getDescrizione());
        if (request.getPrezzo() != null) inserzione.setPrezzo(request.getPrezzo());
        if (request.getDimensioni() != null) inserzione.setDimensioni(request.getDimensioni());
        if (request.getNumero_stanze() != null) inserzione.setNumeroStanze(request.getNumero_stanze());
        if (request.getPiano() != null) inserzione.setPiano(request.getPiano());
        if (request.getAscensore() != null) inserzione.setAscensore(request.getAscensore());
        if (request.getClasse_energetica() != null) inserzione.setClasseEnergetica(request.getClasse_energetica());
        if (request.getCategoria() != null)
            inserzione.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));

        inserzioneRepository.save(inserzione);

        return new DatiInserzioneRequest("Inserzione aggiornata con successo", true);
    }


/*****GET INSERZIONI**************************************************************************************************************************/


    @Override
    public List<InserzioneResponse> ricercaInserzioni(DatiInserzioneFiltriRequest filtri) {

        var specification = InserzioneSpecification.filtra(filtri);

        List<Inserzione> risultati = inserzioneRepository.findAll(specification);

        return risultati.stream()
                .map(map::toInserzioneResponse)
                .toList();
    }


    @Override
    public InserzioneResponse getInserzioneById(Long id) {
        Inserzione inserzione = inserzioneRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Inserzione con ID " + id + " non trovato."));
        return map.toInserzioneResponse(inserzione);
    }

    @Override
    public List<InserzioneCardResponse> getInserzioniRecenti() {

        Pageable limit = PageRequest.of(0, 4);

        List<Inserzione> lista = inserzioneRepository.findUltime4ConFoto(limit);

        return lista.stream()
                .map(inserzione -> {
                    InserzioneCardResponse dto = new InserzioneCardResponse();

                    dto.setIdInserzione(inserzione.getIdInserzione());
                    dto.setTitolo(inserzione.getTitolo());
                    dto.setPrezzo(inserzione.getPrezzo());
                    dto.setDimensioni(inserzione.getDimensioni());
                    dto.setNumero_stanze(inserzione.getNumeroStanze());

                    // Prima foto
                    if (inserzione.getFoto() != null && !inserzione.getFoto().isEmpty()) {
                        dto.setFotoPrincipale(inserzione.getFoto().get(0).getUrlFoto());
                    }

                    return dto;
                })
                .toList();


    }


    @Override
    public List<InserzioneResponse> getAllInserzioni() {
        List<Inserzione> inserzioni = inserzioneRepository.findAllConRelazioni();
        return inserzioni.stream()
                .map(map::toInserzioneResponse)
                .toList();
    }


/*****ELIMINA INSERZIONI**********************************************************************************************************************/


    @Override
    public void eliminaInserzione(Long id, String token) {
        String mailAgente = JwtUtil.extractMail(token);

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovato"));

        Account accountRichiedente = agenteService.getAccountFromToken(token);

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = inserzione.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi eliminare un'inserzione che non hai pubblicato");
        }

        inserzioneRepository.delete(inserzione);
    }



/*******FUNZIONI AUSILIARi*****************************************************************************************************************/

    @Override
    public void cambiaStato(Long id, String token, String nuovoStato) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi modificare lo stato di un'inserzione che non hai pubblicato");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        try {
            StatoInserzione statoEnum = StatoInserzione.valueOf(nuovoStato.toUpperCase());
            inserzione.setStato(statoEnum);
            inserzioneRepository.save(inserzione);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Stato non valido: deve essere DISPONIBILE o VENDUTO");
        }
    }


}
