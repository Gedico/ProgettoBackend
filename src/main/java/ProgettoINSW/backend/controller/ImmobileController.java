package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.immobile.ImmobileFiltriRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.service.ImmobileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/immobili")
public class ImmobileController {

    private final ImmobileService immobileService;

    public ImmobileController(ImmobileService immobileService) {
        this.immobileService = immobileService;
    }


    @PostMapping("/crea")
    public ResponseEntity<InserzioneResponse> creaInserzione(@Valid @RequestBody InserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        // 🔹 Estrai il token dal header (rimuovi la parte "Bearer ")
        String token = authHeader.replace("Bearer ", "").trim();

        // 🔹 Passa tutto al service
        InserzioneResponse response = immobileService.creaInserzione(request, token);

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
