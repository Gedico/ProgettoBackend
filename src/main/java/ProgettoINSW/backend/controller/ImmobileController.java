package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.service.ImmobileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



}
