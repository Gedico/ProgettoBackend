package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.service.*;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.specification.InserzioneSpecification;
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
        IndicatoreProssimita indicatoreProssimita = indicatoreProssimitaService.generaIndicatoriPerInserzione(request);

        Inserzione inserzione = map.inserzioneToEntity(request, posizione, agente , indicatoreProssimita);
        inserzioneRepository.save(inserzione);

        fotoService.processImages(immagini, inserzione);


        return map.toInserzioneResponse(inserzione);
    }




/*****GET INSERZIONI**************************************************************************************************************************/


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

    @Override
    public List<InserzioneCardResponse> getInserzioniPerAgente(String token) {


        final Long idAgente = agenteService.getAgenteFromToken(token).getIdAgente();

        final List<Inserzione> inserzioni = inserzioneRepository.findByAgente_IdAgente(idAgente);

        return inserzioni.stream()
                .map(map::toCardResponse)
                .toList();
    }




}
