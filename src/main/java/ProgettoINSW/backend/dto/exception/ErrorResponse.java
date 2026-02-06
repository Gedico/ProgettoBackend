package ProgettoINSW.backend.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;

    //  Costruttore vuoto (necessario per buildError)
    public ErrorResponse() { // Costruttore vuoto richiesto da Jackson per la deserializzazione JSON
    }


}

//le due aggiunte servono per avere riferimento sia temporale che dello status anche nel frontend.