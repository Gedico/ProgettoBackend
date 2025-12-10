package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.response.SimpleResponse;
import ProgettoINSW.backend.dto.stato.StatoRequest;
import ProgettoINSW.backend.exception.BusinessException;
import ProgettoINSW.backend.service.FotoService;
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
    private final FotoService fotoService;

    public InserzioneController(final InserzioneService inserzioneService,
                                final FotoService fotoService) {
        this.inserzioneService = inserzioneService;
        this.fotoService = fotoService;
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

    @GetMapping("/ricerca")
    public ResponseEntity<List<InserzioneResponse>> ricercaInserzioni(
            @ModelAttribute final DatiInserzioneFiltriRequest filtri) {
        return ResponseEntity.ok(inserzioneService.ricercaInserzioni(filtri));
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

    @DeleteMapping("/eliminaFoto/{id}")
    public ResponseEntity<SimpleResponse> eliminaFoto(
            @PathVariable final Long id,
            @RequestHeader("Authorization") final String authHeader,
            @RequestBody @Valid final List<FotoRequest> daEliminare) {

        final String token = extractToken(authHeader);
        fotoService.eliminaFoto(id, token, daEliminare);
        return ResponseEntity.ok(new SimpleResponse(true, "Foto eliminate con successo"));
    }

    @PutMapping("/modifica/{id}")
    public ResponseEntity<DatiInserzioneRequest> modificaInserzione(
            @PathVariable final Long id,
            @Valid @RequestBody final DatiInserzioneRequest request,
            @RequestHeader("Authorization") final String authHeader) {

        final String token = extractToken(authHeader);
        return ResponseEntity.ok(inserzioneService.modificaInserzione(id, request, token));
    }

    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<SimpleResponse> eliminaInserzione(
            @PathVariable final Long id,
            @RequestHeader("Authorization") final String authHeader) {

        final String token = extractToken(authHeader);
        inserzioneService.eliminaInserzione(id, token);
        return ResponseEntity.ok(new SimpleResponse(true, "Inserzione eliminata con successo"));
    }

    @PutMapping("/modificaStato/{id}")
    public ResponseEntity<SimpleResponse> cambiaStatoInserzione(
            @PathVariable final Long id,
            @RequestHeader("Authorization") final String authHeader,
            @RequestBody @Valid final StatoRequest body) {

        final String token = extractToken(authHeader);
        inserzioneService.cambiaStato(id, token, body.getStato());
        return ResponseEntity.ok(new SimpleResponse(true,
                "Stato aggiornato correttamente a: " + body.getStato()));
    }

    @PostMapping("/caricaFoto/{id}")
    public ResponseEntity<SimpleResponse> caricaFoto(
            @PathVariable final Long id,
            @RequestHeader("Authorization") final String authHeader,
            @RequestBody @Valid final List<FotoRequest> nuoveFoto) {

        final String token = extractToken(authHeader);
        fotoService.caricaFoto(id, token, nuoveFoto);
        return ResponseEntity.ok(new SimpleResponse(true, "Foto caricate con successo"));
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
