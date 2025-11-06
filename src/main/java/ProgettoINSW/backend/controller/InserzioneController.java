package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.dto.datiInserzione.ModificaDatiInserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.service.FotoService;
import ProgettoINSW.backend.service.InserzioneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inserzioni")
public class InserzioneController {

    private final InserzioneService inserzioneService;
    private final FotoService fotoService;

    public InserzioneController(InserzioneService inserzioneService, FotoService fotoService) {
        this.inserzioneService = inserzioneService;
        this.fotoService = fotoService;
    }


    @PostMapping("/crea")
    public ResponseEntity<InserzioneResponse> creaInserzione(@Valid @RequestBody InserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        InserzioneResponse response = inserzioneService.creaInserzione(request, token);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/modifica/{id}")
    public ResponseEntity<ModificaDatiInserzioneRequest> modificaInserzione(@PathVariable("id") Long id, @Valid @RequestBody ModificaDatiInserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        ModificaDatiInserzioneRequest modificaInserzione = inserzioneService.modificaInserzione(id, request, token);

        return ResponseEntity.ok(modificaInserzione );
    }


    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Map<String, Object>> eliminaInserzione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();
        inserzioneService.eliminaInserzione(id, token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inserzione eliminata con successo");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/caricaFoto/{id}")
    public ResponseEntity<Map<String, Object>> caricaFoto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<FotoRequest> nuoveFoto) {

        String token = authHeader.replace("Bearer ", "").trim();
        fotoService.caricaFoto(id, token, nuoveFoto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Foto caricate con successo");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/ricerca")
    public ResponseEntity<List<InserzioneResponse>> ricercaInserzioni(@ModelAttribute DatiInserzioneFiltriRequest filtri) {List<InserzioneResponse> risultati = inserzioneService.ricercaInserzioni(filtri);

        if (risultati.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(risultati);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InserzioneResponse> getInserzioneById(@PathVariable("id") Long id) {
        InserzioneResponse response = inserzioneService.getInserzioneById(id);
        return ResponseEntity.ok(response);
    }

}
