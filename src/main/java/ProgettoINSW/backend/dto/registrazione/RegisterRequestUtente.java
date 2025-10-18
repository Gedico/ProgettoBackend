package ProgettoINSW.backend.dto.registrazione;

public class RegisterRequestUtente {
    private String nome;
    private String cognome;
    private String mail;
    private String password;
    private String numero;
    private String indirizzo;
    private String messaggio;

    //Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getMessaggio() { return messaggio; }   // <-- corretto
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; } // <-- corretto
}

