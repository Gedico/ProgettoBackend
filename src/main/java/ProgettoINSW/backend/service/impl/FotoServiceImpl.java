package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.repository.FotoRepository;
import ProgettoINSW.backend.repository.InserzioneRepository;
import ProgettoINSW.backend.service.FotoService;
import ProgettoINSW.backend.util.ValidazioneUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FotoServiceImpl implements FotoService {

    private final InserzioneRepository inserzioneRepository;
    private final FotoRepository fotoRepository;
    private final ValidazioneUtil validazioneUtil;

    @Transactional
    @Override
    public void caricaFoto(Long id, String token, List<FotoRequest> nuoveFoto) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi caricare foto per un'iserzione che non hai pubblicato");
        }

        if (nuoveFoto == null || nuoveFoto.isEmpty()) {
            throw new RuntimeException("Nessuna foto fornita");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        List<Foto> fotoDaSalvare = nuoveFoto.stream()
                .map(f -> {
                    Foto foto = new Foto();
                    foto.setUrlFoto(f.getUrl());
                    foto.setInserzione(inserzione);
                    return foto;
                })
                .collect(Collectors.toList());

        fotoRepository.saveAll(fotoDaSalvare);

    }

    @Transactional
    @Override
    public void eliminaFoto(Long id, String token, List<FotoRequest> daEliminare) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi eliminare foto per un'inserzione che non hai pubblicato");
        }

        if (daEliminare == null || daEliminare.isEmpty()) {
            throw new RuntimeException("Nessuna foto fornita");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        List<String> urlDaEliminare = daEliminare.stream()
                .map(FotoRequest::getUrl)
                .collect(Collectors.toList());

        List<Foto> fotoPresenti = fotoRepository.findByInserzioneAndUrlFotoIn(inserzione, urlDaEliminare);

        if (fotoPresenti.isEmpty()) {
            throw new RuntimeException("Nessuna foto trovata da eliminare");
        }

        fotoRepository.deleteAll(fotoPresenti);
    }



}
