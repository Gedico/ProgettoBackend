package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.Proposta;
import org.springframework.stereotype.Component;

@Component
public class PropostaMap {
    public PropostaResponse toPropostaResponse(Proposta entity, String messaggio) {
        PropostaResponse dto = new PropostaResponse();
        dto.setIdProposta(entity.getIdProposta());
        dto.setImporto(entity.getPrezzoProposta());
        dto.setStato(entity.getStato());
        dto.setTitoloInserzione(entity.getInserzione().getTitolo());
        dto.setDataCreazione(entity.getDataProposta());
        dto.setMessaggio(messaggio);

        return dto;
    }

}
