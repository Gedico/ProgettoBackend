package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.profilo.*;
import ProgettoINSW.backend.service.ProfiloService;
import ProgettoINSW.backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/profilo")
public class ProfiloController {

    private final ProfiloService profiloService;

    public ProfiloController(ProfiloService profiloService) {
        this.profiloService = profiloService;
    }

    @GetMapping("/me") //fa riferimento al token passato
    public ResponseEntity<ProfiloResponse> getProfilo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String mail = JwtUtil.extractMail(token);

        ProfiloResponse response = profiloService.getProfilo(mail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateProfiloResponse> aggiornaProfilo(
            @RequestBody UpdateProfiloRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Estrai il token
        String token = authHeader.substring(7); // rimuove "Bearer "
        String mail = JwtUtil.extractMail(token);

        UpdateProfiloResponse response = profiloService.aggiornaProfilo(request, mail);
        return ResponseEntity.ok(response);
    }
}

