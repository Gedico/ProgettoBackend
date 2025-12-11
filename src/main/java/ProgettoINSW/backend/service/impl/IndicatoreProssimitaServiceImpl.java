package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.geopify.GeoapifyResponse;
import ProgettoINSW.backend.model.IndicatoreProssimita;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.repository.IndicatoreProssimitaRepository;
import ProgettoINSW.backend.service.GeoapifyClient;
import ProgettoINSW.backend.service.IndicatoreProssimitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndicatoreProssimitaServiceImpl implements IndicatoreProssimitaService {

    private final IndicatoreProssimitaRepository indicatoreRepo;
    private final GeoapifyClient geopify;

    @Override
    public void generaIndicatoriPerInserzione(Inserzione inserzione) {

        GeoapifyResponse response = geopify.cercaLuoghi(
                inserzione.getPosizione().getLatitudine(),
                inserzione.getPosizione().getLongitudine()
        );

        boolean vicinoScuola = false;
        boolean vicinoParco = false;
        boolean vicinoMezziPubblici = false;

        if (response != null && response.getFeatures() != null) {
            for (var feature : response.getFeatures()) {

                if (feature.getProperties() == null) continue;

                var cats = feature.getProperties().getCategories();
                if (cats == null) continue;

                if (cats.contains("education.school"))
                    vicinoScuola = true;

                if (cats.contains("leisure.park"))
                    vicinoParco = true;

                if (cats.stream().anyMatch(c -> c.contains("public_transport")))
                    vicinoMezziPubblici = true;
            }
        }

        IndicatoreProssimita indicatore = new IndicatoreProssimita();
        indicatore.setInserzione(inserzione);
        indicatore.setVicinoScuola(vicinoScuola);
        indicatore.setVicinoParco(vicinoParco);
        indicatore.setVicinoMezziPubblici(vicinoMezziPubblici);

        indicatoreRepo.save(indicatore);
    }
}

