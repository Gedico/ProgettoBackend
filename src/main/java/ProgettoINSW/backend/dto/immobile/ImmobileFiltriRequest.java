package ProgettoINSW.backend.dto.immobile;
import ProgettoINSW.backend.model.enums.Categoria;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ImmobileFiltriRequest {
    private String citta;
    private Categoria categoria;
    private BigDecimal prezzoMin;
    private BigDecimal prezzoMax;
    private Double dimensioniMin;
    private Double dimensioniMax;
}
