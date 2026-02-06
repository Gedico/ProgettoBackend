package ProgettoINSW.backend.controller;

import ProgettoINSW.backend.dto.profilo.*;
import ProgettoINSW.backend.service.ProfiloService;
import ProgettoINSW.backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ProgettoINSW.backend.dto.profilo.ChangePasswordRequest;
import java.util.Map;


@RestController
@RequestMapping("/api/profilo")
public class ProfiloController {

    private static final String MESSAGE_KEY = "message";
    private static final String BEARER_PREFIX = "Bearer ";

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

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Header Authorization non valido");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        String mail = JwtUtil.extractMail(token);

        profiloService.changePassword(mail, request);

        return ResponseEntity.ok(
                Map.of(MESSAGE_KEY, "Password aggiornata con successo")
        );
    }



}
