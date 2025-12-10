package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
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
    public ResponseEntity<List<InserzioneResponse>> ricercaInserzioni(@ModelAttribute DatiInserzioneFiltriRequest filtri) {
        List<InserzioneResponse> risultati = inserzioneService.ricercaInserzioni(filtri);

        return ResponseEntity.ok(risultati);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InserzioneResponse> getInserzioneById(@PathVariable("id") Long id) {
        InserzioneResponse response = inserzioneService.getInserzioneById(id);
        return ResponseEntity.ok(response);
    }

  // Dashboard di agente
    @GetMapping("/mie")
    public ResponseEntity<List<InserzioneCardResponse>> getInserzioniAgente(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        List<InserzioneCardResponse> mie = inserzioneService.getInserzioniPerAgente(token);

        return ResponseEntity.ok(mie);
    }



    //Gestione inserzioni
/******************************************************************************************************************/

    @PostMapping(value = "/crea",consumes = { "multipart/form-data", "application/json" })
            public ResponseEntity<InserzioneResponse> creaInserzione(
            @RequestPart(value = "dati", required = false) InserzioneRequest request,
            @RequestPart(value = "immagini", required = false) MultipartFile[] immagini,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {

        String token = extractToken(authHeader);
         InserzioneResponse response = inserzioneService.creaInserzione(request, immagini, token);

         return ResponseEntity.ok(response);
     }



    @DeleteMapping("/eliminaFoto/{id}")
    public ResponseEntity<SimpleResponse> eliminaFoto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid List<FotoRequest> daEliminare) {

        String token = extractToken(authHeader);
        fotoService.eliminaFoto(id, token, daEliminare);

        return ResponseEntity.ok(new SimpleResponse(true, "Foto eliminate con successo"));
    }


    @PutMapping("/modifica/{id}")
    public ResponseEntity<DatiInserzioneRequest> modificaInserzione(@PathVariable("id") Long id, @Valid @RequestBody DatiInserzioneRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        DatiInserzioneRequest modificaInserzione = inserzioneService.modificaInserzione(id, request, token);

        return ResponseEntity.ok(modificaInserzione);
    }


    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<SimpleResponse> eliminaInserzione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        inserzioneService.eliminaInserzione(id, token);

        return ResponseEntity.ok(new SimpleResponse(true, "Inserzione eliminata con successo"));
    }

    @PutMapping("/modificaStato/{id}")
    public ResponseEntity<SimpleResponse> cambiaStatoInserzione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid StatoRequest body) {

        String token = extractToken(authHeader);
        String nuovoStato = body.getStato();

        inserzioneService.cambiaStato(id, token, nuovoStato);

        return ResponseEntity.ok(new SimpleResponse(true, "Stato aggiornato correttamente a: " + nuovoStato));
    }


    @PostMapping("/caricaFoto/{id}")
    public ResponseEntity<SimpleResponse> caricaFoto(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid List<FotoRequest> nuoveFoto) {

        String token = extractToken(authHeader);
        fotoService.caricaFoto(id, token, nuoveFoto);

        return ResponseEntity.ok(new SimpleResponse(true, "Foto caricate con successo"));
    }


/******FUNZIONI AUSILIARIE*******************************************************************************************************************************/

private String extractToken(String header) {
    if (header == null || !header.startsWith("Bearer "))
        throw new BusinessException("Token non fornito");

    return header.substring(7).trim();
}



}
