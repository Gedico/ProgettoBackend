package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.offerta.AggiornaStatoOffertaRequest;
import ProgettoINSW.backend.dto.offerta.OffertaResponse;
import ProgettoINSW.backend.model.enums.StatoOfferta;

import java.util.List;

public interface OffertaService {
    List<OffertaResponse> getOfferteAgente(String token, StatoOfferta stato);
    OffertaResponse aggiornaStatoOfferta(Long idOfferta, AggiornaStatoOffertaRequest request, String token);
}
