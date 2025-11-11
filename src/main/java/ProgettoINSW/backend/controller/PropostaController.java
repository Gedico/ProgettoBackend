package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.service.PropostaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposte")
public class PropostaController {
    private final PropostaService propostaService;

    public PropostaController(PropostaService propostaService) {
        this.propostaService = propostaService;
    }

    @GetMapping
    public ResponseEntity<List<PropostaResponse>> getProposteAgente(
            @RequestHeader("Authorization") String authHeader){

        String token = authHeader.replace("Bearer ", "").trim();
        List<PropostaResponse> proposte = propostaService.getProposteAgente(token);

        if (proposte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(proposte);
    }

    @GetMapping("/stato")
    public ResponseEntity<List<PropostaResponse>> getProposteAgente(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) StatoProposta stato) {

        String token = authHeader.replace("Bearer ", "").trim();
        List<PropostaResponse> proposte = propostaService.getProposteAgenteStato(token, stato);

        if (proposte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(proposte);
    }

    @PutMapping("/{id}/stato")
    public ResponseEntity<PropostaResponse> aggiornaStatoProposta(
            @PathVariable("id") Long idProposta,
            @RequestBody AggiornaStatoPropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        PropostaResponse response = propostaService.aggiornaStatoProposta(idProposta, request, token);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PropostaResponse> dettagliProposta(@PathVariable("id") Long idProposta,
                                                             @RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "").trim();

        PropostaResponse response = propostaService.mostraDettagliProposta(idProposta, token);

        return ResponseEntity.ok(response);
    }


}
