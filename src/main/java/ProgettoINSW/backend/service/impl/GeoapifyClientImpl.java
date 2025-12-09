package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.geopify.GeoapifyResponse;
import ProgettoINSW.backend.service.GeoapifyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GeoapifyClientImpl implements GeoapifyClient {

    private final RestTemplate restTemplate;

    @Value("${geoapify.api.key}")
    private String apiKey;

    @Override
    public GeoapifyResponse cercaLuoghi(BigDecimal lat, BigDecimal lon) {

        String url =
                "https://api.geoapify.com/v2/places?categories=" +
                        "education.school," +
                        "healthcare.hospital," +
                        "commercial.supermarket," +
                        "leisure.park," +
                        "public_transport," +
                        "catering.restaurant" +
                        "&filter=circle:" + lon + "," + lat + ",1000" +
                        "&limit=50" +
                        "&apiKey=" + apiKey;

        return restTemplate.getForObject(url, GeoapifyResponse.class);
    }
}

