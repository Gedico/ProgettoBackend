package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.proposta.*;
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

    /* =========================
       UTILITY
       ========================= */

    private String extractToken(String authHeader) {
        return authHeader.replace("Bearer ", "").trim();
    }

    /* =========================
       PROPOSTE AGENTE (manuale e online)
       ========================= */

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteAgente(
            @RequestHeader("Authorization") String authHeader) {

        List<PropostaResponse> proposte =
                propostaService.getProposteAgente(extractToken(authHeader));

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }

    @GetMapping("/filtra")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteAgenteByStato(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) StatoProposta stato) {

        List<PropostaResponse> proposte =
                propostaService.getProposteAgenteStato(extractToken(authHeader), stato);

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }

    @GetMapping("/registro")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<List<PropostaResponse>> registroProposte(
            @RequestHeader("Authorization") String authHeader) {

        List<PropostaResponse> registro =
                propostaService.getProposteAgenteRegistro(extractToken(authHeader));

        return registro.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(registro);
    }

    @PutMapping("/{id}/stato")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<PropostaResponse> aggiornaStatoProposta(
            @PathVariable Long id,
            @RequestBody AggiornaStatoPropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                propostaService.aggiornaStatoProposta(id, request, extractToken(authHeader))
        );
    }

    @PostMapping("/{id}/controproposta")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<PropostaResponse> creaControproposta(
            @PathVariable Long id,
            @RequestBody @Valid ContropropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                propostaService.creaControproposta(id, request, extractToken(authHeader))
        );
    }

    @PostMapping("/inserzioni/{idInserzione}/manuale")
    @PreAuthorize("hasRole('AGENTE')")
    public ResponseEntity<PropostaResponse> creaPropostaManuale(
            @PathVariable Long idInserzione,
            @RequestBody @Valid PropostaManualeRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                propostaService.creaPropostaManuale(
                        idInserzione,
                        request,
                        extractToken(authHeader)
                )
        );
    }


    /* =========================
       PROPOSTE UTENTE
       ========================= */

    @PostMapping
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<PropostaResponse> inviaProposta(
            @RequestBody @Valid PropostaRequest request,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                propostaService.inviaProposta(request, extractToken(authHeader))
        );
    }

    @GetMapping("/mie")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<List<PropostaResponse>> getProposteUtente(
            @RequestHeader("Authorization") String authHeader) {

        List<PropostaResponse> proposte =
                propostaService.getProposteUtente(extractToken(authHeader));

        return proposte.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(proposte);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<String> eliminaProposta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        propostaService.eliminaProposta(id, extractToken(authHeader));
        return ResponseEntity.ok("Proposta eliminata con successo.");
    }

    /* =========================
       DETTAGLI
       ========================= */

    @GetMapping("/{id}")
    public ResponseEntity<PropostaResponse> dettagliProposta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(
                propostaService.mostraDettagliProposta(id, extractToken(authHeader))
        );
    }
}
