package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.model.Posizione;
import ProgettoINSW.backend.repository.PosizioneRepository;
import ProgettoINSW.backend.service.PosizioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PosizioneServiceImpl implements PosizioneService {

    private final PosizioneRepository posizioneRepository;

    @Override
    public Posizione creaPosizione(PosizioneRequest dto) {
        Posizione pos = new Posizione();
        pos.setLatitudine(dto.getLatitudine());
        pos.setLongitudine(dto.getLongitudine());
        pos.setComune(dto.getComune());
        pos.setIndirizzo(dto.getIndirizzo());

        return posizioneRepository.save(pos);
    }
}
