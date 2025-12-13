package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.Proposta;
import org.springframework.stereotype.Component;

@Component
public class PropostaMap {

    public PropostaResponse toPropostaResponse(Proposta entity, String messaggio) {

        PropostaResponse dto = new PropostaResponse();

        // ID proposta
        dto.setIdProposta(entity.getIdProposta());

        // Inserzione
        dto.setIdInserzione(entity.getInserzione().getIdInserzione());
        dto.setTitoloInserzione(entity.getInserzione().getTitolo());
        dto.setPrezzoInserzione(entity.getInserzione().getPrezzo());

        // Importo e stato
        dto.setImporto(entity.getPrezzoProposta());
        dto.setStato(entity.getStato());
        dto.setDataCreazione(entity.getDataProposta());

        // Proponente
        dto.setProponente(entity.getProponente());
        dto.setIdUtente(entity.getCliente().getIdUtente());
        dto.setIdAgente(entity.getAgente().getIdAgente());

        // Controproposta
        if (entity.getPropostaPrecedente() != null) {
            dto.setIdPropostaPrecedente(
                    entity.getPropostaPrecedente().getIdProposta()
            );
        }

        // Messaggio opzionale
        dto.setMessaggio(messaggio);

        return dto;
    }
}

