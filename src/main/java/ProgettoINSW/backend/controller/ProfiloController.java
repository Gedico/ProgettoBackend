package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.profilo.*;
import ProgettoINSW.backend.service.ProfiloService;
import ProgettoINSW.backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profilo")
public class ProfiloController {

    private final ProfiloService profiloService;

    public ProfiloController(ProfiloService profiloService) {
        this.profiloService = profiloService;
    }

    @GetMapping
    public ResponseEntity<ProfiloResponse> getProfilo(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String mail = JwtUtil.extractMail(token);

        return ResponseEntity.ok(profiloService.getProfilo(mail));
    }

    @PutMapping
    public ResponseEntity<UpdateProfiloResponse> aggiornaProfilo(
            @RequestBody UpdateProfiloRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String mail = JwtUtil.extractMail(token);

        return ResponseEntity.ok(profiloService.aggiornaProfilo(request, mail));
    }
}
