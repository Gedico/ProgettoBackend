package ProgettoINSW.backend.dto.indicatori;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndicatoreResponse {

    private boolean scuoleVicine;
    private boolean supermercatiVicini;
    private boolean mezziPubbliciVicini;
}

