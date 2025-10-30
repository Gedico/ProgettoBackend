package ProgettoINSW.backend.dto.registrazione;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    //Getter e Setter
    private String nome;
    private String cognome;
    private String mail;
    private String password;
    private String numero;
    private String indirizzo;


    @Setter
    @Getter
    private String approfondimento; //non ho capito

    @Setter
    @Getter
    private String messaggio;


}