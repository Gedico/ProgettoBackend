package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.Proposta;
import ProgettoINSW.backend.model.enums.TipoProposta;
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
        dto.setIdAgente(entity.getAgente().getIdAgente());

        // SOLO SE ONLINE
        if (entity.getCliente() != null) {
            dto.setIdUtente(entity.getCliente().getIdUtente());
        }

        // SOLO SE MANUALE
        if (entity.getTipo() == TipoProposta.MANUALE) {
            dto.setNomeCliente(entity.getNomeCliente());
            dto.setContattoCliente(entity.getContattoCliente());
        }

        // Controproposta
        if (entity.getPropostaPrecedente() != null) {
            dto.setIdPropostaPrecedente(
                    entity.getPropostaPrecedente().getIdProposta()
            );
        }

        dto.setTipo(entity.getTipo());
        dto.setMessaggio(messaggio);

        return dto;
    }
}
