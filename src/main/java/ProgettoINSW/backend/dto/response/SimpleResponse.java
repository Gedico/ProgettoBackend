package ProgettoINSW.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class SimpleResponse {
    private boolean success;
    private String message;

    public SimpleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}

