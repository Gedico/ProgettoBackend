package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.offerta.AggiornaStatoOffertaRequest;
import ProgettoINSW.backend.dto.offerta.OffertaResponse;
import ProgettoINSW.backend.model.enums.StatoOfferta;
import ProgettoINSW.backend.service.OffertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offerte")
public class OffertaController {
    private final OffertaService offertaService;

    public OffertaController(OffertaService offertaService) {
        this.offertaService = offertaService;
    }

    @GetMapping
    public ResponseEntity<List<OffertaResponse>> getOfferteAgente(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) StatoOfferta stato) {

        String token = authHeader.replace("Bearer ", "").trim();
        List<OffertaResponse> offerte = offertaService.getOfferteAgente(token, stato);

        if (offerte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(offerte);
    }

    @PutMapping("/{id}/stato")
    public ResponseEntity<OffertaResponse> aggiornaStatoOfferta(
            @PathVariable("id") Long idOfferta,
            @RequestBody AggiornaStatoOffertaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        OffertaResponse response = offertaService.aggiornaStatoOfferta(idOfferta, request, token);

        return ResponseEntity.ok(response);
    }


}
