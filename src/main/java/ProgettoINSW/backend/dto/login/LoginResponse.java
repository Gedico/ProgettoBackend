package ProgettoINSW.backend.dto.login;

public class LoginResponse {
    private String messaggio;
    private String ruolo;

    public LoginResponse() {}
    public LoginResponse(String messaggio, String ruolo) {
        this.messaggio = messaggio;
        this.ruolo = ruolo;
    }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

}
