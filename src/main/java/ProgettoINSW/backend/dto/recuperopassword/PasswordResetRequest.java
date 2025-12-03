package ProgettoINSW.backend.dto.recuperopassword;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PasswordResetRequest {

    @Setter
    private String email;

}

