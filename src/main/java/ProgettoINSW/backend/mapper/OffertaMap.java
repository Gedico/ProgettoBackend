package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.offerta.OffertaResponse;
import ProgettoINSW.backend.model.Offerta;
import org.springframework.stereotype.Component;

@Component
public class OffertaMap {
    public OffertaResponse toDto(Offerta entity, String messaggio) {
        OffertaResponse dto = new OffertaResponse();
        dto.setIdOfferta(entity.getIdOfferta());
        dto.setImporto(entity.getPrezzoOfferta());
        dto.setStato(entity.getStato());
        dto.setTitoloImmobile(entity.getImmobile().getTitolo());
        dto.setDataCreazione(entity.getDataOfferta());
        dto.setMessaggio(messaggio);

        return dto;
    }
}
