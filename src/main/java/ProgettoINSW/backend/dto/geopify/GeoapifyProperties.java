package ProgettoINSW.backend.dto.geopify;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GeoapifyProperties {

    private String name;
    private List<String> categories;
    private BigDecimal distance;

}
