package ProgettoINSW.backend.dto.profilo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfiloResponse {
    private String messaggio;
    private boolean esito;
}
