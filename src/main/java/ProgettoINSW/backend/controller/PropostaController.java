package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.service.PropostaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposte")
@RequiredArgsConstructor
public class PropostaController {

    private final PropostaService propostaService;

    // 🔹 UTILITY: rimuove il prefisso Bearer
    private String cleanToken(String header) {
        return header.replace("Bearer ", "").trim();
    }

    // 🔹 1) Proposte dell'agente autenticato
    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteAgente(
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);
        List<PropostaResponse> proposte = propostaService.getProposteAgente(token);

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }

    // 🔹 2) Proposte dell'agente filtrate per stato
    @GetMapping("/filtra")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteAgenteByStato(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) StatoProposta stato) {

        String token = cleanToken(authHeader);
        List<PropostaResponse> proposte = propostaService.getProposteAgenteStato(token, stato);

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }

    // 🔹 3) Aggiorna lo stato (ACCETTA / RIFIUTA)
    @PutMapping("/{id}/stato")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<PropostaResponse> aggiornaStatoProposta(
            @PathVariable Long id,
            @RequestBody AggiornaStatoPropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);
        return ResponseEntity.ok(
                propostaService.aggiornaStatoProposta(id, request, token)
        );
    }

    // 🔹 4) Dettagli proposta
    @GetMapping("/{id}")
    public ResponseEntity<PropostaResponse> dettagliProposta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);
        return ResponseEntity.ok(
                propostaService.mostraDettagliProposta(id, token)
        );
    }

    // 🔹 5) Invia una nuova proposta per una inserzione
    @PostMapping
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<PropostaResponse> inviaProposta(
            @RequestBody @Valid PropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);
        return ResponseEntity.ok(
                propostaService.inviaProposta(request, token)
        );
    }

    // 🔹 6) Elimina una proposta (solo UTENTE e solo se IN_ATTESA)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<String> eliminaProposta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);
        propostaService.eliminaProposta(id, token);

        return ResponseEntity.ok("Proposta eliminata con successo.");
    }

    // 🔹 7) Proposte inviate dall'utente autenticato
    @GetMapping("/mie")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteUtente(
            @RequestHeader("Authorization") String authHeader) {

        String token = cleanToken(authHeader);

        List<PropostaResponse> proposte = propostaService.getProposteUtente(token);

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }


}
