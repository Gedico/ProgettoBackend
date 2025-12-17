package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.proposta.*;
import ProgettoINSW.backend.model.enums.StatoProposta;

import java.util.List;

public interface PropostaService {

    /* =========================
       LETTURA PROPOSTE
       ========================= */

    List<PropostaResponse> getProposteAgente(String token);
    List<PropostaResponse> getProposteAgenteStato(String token, StatoProposta stato);
    List<PropostaResponse> getProposteAgenteRegistro(String token);
    List<PropostaResponse> getProposteUtente(String token);
    PropostaResponse mostraDettagliProposta(Long idProposta, String token);

    /* =========================
       CREAZIONE / MODIFICA
       ========================= */

    PropostaResponse inviaProposta(PropostaRequest request, String token);
    PropostaResponse creaControproposta(Long idProposta, ContropropostaRequest request, String token);
    PropostaResponse aggiornaStatoProposta(Long id, AggiornaStatoPropostaRequest request, String token);

    PropostaResponse creaPropostaManuale(Long idInserzione, PropostaManualeRequest request, String token);

    /* =========================
       ELIMINAZIONE
       ========================= */

    void eliminaProposta(Long idProposta, String token);

}

