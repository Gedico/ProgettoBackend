package ProgettoINSW.backend.dto.posizione;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

    @Getter
    @Setter
    public class PosizioneRequest {

        private BigDecimal latitudine;
        private BigDecimal longitudine;
        private String comune;
        private String indirizzo;

    }
