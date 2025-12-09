package ProgettoINSW.backend.dto.geopify;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeoapifyResponse {
    private List<GeoapifyFeature> features;
}
