package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import ProgettoINSW.backend.dto.inserzionesearch.InserzioneSearchResponse;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.repository.InserzioneRepository;
import ProgettoINSW.backend.specification.InserzioneSpecification;
import ProgettoINSW.backend.service.InserzioneSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InserzioneSearchServiceImpl implements InserzioneSearchService {

    private final InserzioneRepository inserzioneRepository;

    @Override
    public List<InserzioneSearchResponse> ricercaInserzioni(InserzioneSearchRequest request) {

        List<Inserzione> inserzioni = inserzioneRepository.findAll(
                InserzioneSpecification.filtra(request)
        );

        return inserzioni.stream()
                .map(this::toSearchResponse)
                .collect(Collectors.toList());
    }

    private InserzioneSearchResponse toSearchResponse(Inserzione inserzione) {
        InserzioneSearchResponse dto = new InserzioneSearchResponse();

        dto.setIdInserzione(inserzione.getIdInserzione());
        dto.setTitolo(inserzione.getTitolo());
        dto.setDescrizione(inserzione.getDescrizione());
        dto.setComune(inserzione.getPosizione().getComune());
        dto.setLatitudine(inserzione.getPosizione().getLatitudine());
        dto.setLongitudine(inserzione.getPosizione().getLongitudine());
        dto.setPrezzo(inserzione.getPrezzo());
        dto.setDimensione(inserzione.getDimensioni());
        dto.setStanze(inserzione.getNumeroStanze());
        dto.setPiano(inserzione.getPiano());
        dto.setAscensore(inserzione.getAscensore());
        dto.setClasseEnergetica(inserzione.getClasseEnergetica());
        dto.setFotoUrls(
                inserzione.getFoto() != null ?
                        inserzione.getFoto().stream().map(f -> f.getUrlFoto()).collect(Collectors.toList()) :
                        List.of()
        );
        dto.setTipo(inserzione.getCategoria());

        return dto;
    }

}

