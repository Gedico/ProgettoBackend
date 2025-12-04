package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.service.FotoService;
import ProgettoINSW.backend.service.InserzioneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
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

    //Visualizzazione inserzioni
/******************************************************************************************************************/

    @GetMapping
    public ResponseEntity<List<InserzioneResponse>> getAllInserzioni() {
        List<InserzioneResponse> inserzioni = inserzioneService.getAllInserzioni();
        return ResponseEntity.ok(inserzioni);
    }


    @GetMapping("/recenti")
    public ResponseEntity<List<InserzioneCardResponse>> getInserzioniRecenti() {
        List<InserzioneCardResponse> inserzioniRecenti = inserzioneService.getInserzioniRecenti();
        return ResponseEntity.ok(inserzioniRecenti);
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



    //Gestione inserzioni
/******************************************************************************************************************/

        @PostMapping(value = "/crea", consumes = "multipart/form-data")
            public InserzioneResponse creaInserzione(
            @RequestPart("dati") InserzioneRequest request,
           @RequestPart("immagini") MultipartFile[] immagini,
          @RequestHeader("Authorization") String token
        ) throws IOException {
          return inserzioneService.creaInserzione(request, immagini, token);
        }


    @DeleteMapping("/eliminaFoto/{id}")
    public ResponseEntity<Map<String, Object>> eliminaFoto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<FotoRequest> daEliminare) {

        String token = authHeader.replace("Bearer ", "").trim();
        fotoService.eliminaFoto(id, token, daEliminare);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Foto eliminate con successo");
        response.put("success", true);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/modifica/{id}")
    public ResponseEntity<DatiInserzioneRequest> modificaInserzione(@PathVariable("id") Long id, @Valid @RequestBody DatiInserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();

        DatiInserzioneRequest modificaInserzione = inserzioneService.modificaInserzione(id, request, token);

        return ResponseEntity.ok(modificaInserzione);
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

    @PutMapping("/modificaStato/{id}")
    public ResponseEntity<String> cambiaStatoInserzione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {

        try {
            String nuovoStato = body.get("stato");
            inserzioneService.cambiaStato(id, token.replace("Bearer ", ""), nuovoStato);
            return ResponseEntity.ok("Stato dell'inserzione aggiornato correttamente a: " + nuovoStato);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        }
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

}
