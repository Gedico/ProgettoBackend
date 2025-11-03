package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.immobile.ImmobileFiltriRequest;
import ProgettoINSW.backend.dto.immobile.ModificaImmobileRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.service.ImmobileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/immobili")
public class ImmobileController {

    private final ImmobileService immobileService;

    public ImmobileController(ImmobileService immobileService) {
        this.immobileService = immobileService;
    }


    @PostMapping("/crea")
    public ResponseEntity<InserzioneResponse> creaInserzione(@Valid @RequestBody InserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        InserzioneResponse response = immobileService.creaInserzione(request, token);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/modifica/{id}")
    public ResponseEntity<ModificaImmobileRequest> modificaInserzione(@PathVariable("id") Long id, @Valid @RequestBody ModificaImmobileRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        ModificaImmobileRequest modificaInserzione = immobileService.modificaInserzione(id, request, token);

        return ResponseEntity.ok(modificaInserzione );
    }


    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Map<String, Object>> eliminaInserzione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();
        immobileService.eliminaInserzione(id, token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Immobile eliminato con successo");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/caricaFoto/{id}")
    public ResponseEntity<Map<String, Object>> caricaFotoImmobile(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<FotoRequest> nuoveFoto) {

        String token = authHeader.replace("Bearer ", "").trim();
        immobileService.caricaFotoImmobile(id, token, nuoveFoto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Foto caricate con successo");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/ricerca")
    public ResponseEntity<List<InserzioneResponse>> ricercaImmobili(@ModelAttribute ImmobileFiltriRequest filtri) {List<InserzioneResponse> risultati = immobileService.ricercaImmobili(filtri);

        if (risultati.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(risultati);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InserzioneResponse> getInserzioneById(@PathVariable("id") Long id) {
        InserzioneResponse response = immobileService.getInserzioneById(id);
        return ResponseEntity.ok(response);
    }

}
