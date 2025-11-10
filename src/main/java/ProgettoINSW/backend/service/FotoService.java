package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.foto.FotoRequest;

import java.util.List;

public interface FotoService {

    void caricaFoto(Long id, String token, List<FotoRequest> nuoveFoto);

    void eliminaFoto(Long id, String token, List<FotoRequest> daEliminare);
}
