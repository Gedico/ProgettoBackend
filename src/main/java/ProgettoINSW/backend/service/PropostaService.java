package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.enums.StatoProposta;

import java.util.List;

public interface PropostaService {

    List<PropostaResponse> getProposteAgente(String token);
    List<PropostaResponse> getProposteAgenteStato(String token, StatoProposta stato);
    PropostaResponse aggiornaStatoProposta(Long id, AggiornaStatoPropostaRequest request, String token);

    PropostaResponse mostraDettagliProposta(Long idProposta, String token);
}
