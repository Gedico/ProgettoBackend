package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.datiInserzione.InserzioneSearchRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.inserzionesearch.*;
import ProgettoINSW.backend.exception.BusinessException;
import ProgettoINSW.backend.service.InserzioneSearchService;
import ProgettoINSW.backend.service.InserzioneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inserzioni")
public class InserzioneController {

    private final InserzioneService inserzioneService;
    private final InserzioneSearchService inserzioneSearchService;

    public InserzioneController(final InserzioneService inserzioneService,
                                final InserzioneSearchService inserzioneSearchService) {
        this.inserzioneService = inserzioneService;
        this.inserzioneSearchService = inserzioneSearchService ;
    }

    // -----------------------------------------------------------
    // VISUALIZZAZIONE INSERZIONI
    // -----------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<InserzioneResponse>> getAllInserzioni() {
        return ResponseEntity.ok(inserzioneService.getAllInserzioni());
    }

    @GetMapping("/recenti")
    public ResponseEntity<List<InserzioneCardResponse>> getInserzioniRecenti() {
        return ResponseEntity.ok(inserzioneService.getInserzioniRecenti());
    }

    @PostMapping("/search")
    public ResponseEntity<List<InserzioneSearchResponse>> ricercaInserzioni(
            @Valid @RequestBody InserzioneSearchRequest request) {

        List<InserzioneSearchResponse> risultati =
                inserzioneSearchService.ricercaInserzioni(request);

        return ResponseEntity.ok(risultati);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InserzioneResponse> getInserzioneById(
            @PathVariable final Long id) {
        return ResponseEntity.ok(inserzioneService.getInserzioneById(id));
    }

    // -----------------------------------------------------------
    // DASHBOARD AGENTE
    // -----------------------------------------------------------

    @GetMapping("/mie")
    public ResponseEntity<List<InserzioneCardResponse>> getInserzioniAgente(
            @RequestHeader("Authorization") final String authHeader) {

        final String token = extractToken(authHeader);
        return ResponseEntity.ok(inserzioneService.getInserzioniPerAgente(token));
    }

    // -----------------------------------------------------------
    // GESTIONE INSERZIONI
    // -----------------------------------------------------------

    @PostMapping(value = "/crea", consumes = { "multipart/form-data", "application/json" })
    public ResponseEntity<InserzioneResponse> creaInserzione(
            @RequestPart(value = "dati", required = false) final InserzioneRequest request,
            @RequestPart(value = "immagini", required = false) final MultipartFile[] immagini,
            @RequestHeader("Authorization") final String authHeader) throws IOException {

        final String token = extractToken(authHeader);
        return ResponseEntity.ok(inserzioneService.creaInserzione(request, immagini, token));
    }


    // -----------------------------------------------------------
    // TOKEN UTIL
    // -----------------------------------------------------------

    private String extractToken(final String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException("Token non fornito");
        }
        return header.substring(7).trim();
    }
}
