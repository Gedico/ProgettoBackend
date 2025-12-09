package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.geopify.GeoapifyResponse;

import java.math.BigDecimal;

public interface GeoapifyClient {

    /**
     * Restituisce luoghi nelle vicinanze della posizione specificata.
     */
    GeoapifyResponse cercaLuoghi(BigDecimal lat, BigDecimal lon);
}


