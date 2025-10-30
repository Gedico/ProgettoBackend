package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.profilo.*;

public interface ProfiloService {
    ProfiloResponse getProfilo(String mail);
    UpdateProfiloResponse aggiornaProfilo(UpdateProfiloRequest request, String mail);
}
