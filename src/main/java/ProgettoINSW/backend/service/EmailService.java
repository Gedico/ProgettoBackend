package ProgettoINSW.backend.service;

public interface EmailService {


    void sendPasswordResetEmail(String to, String resetLink);
}


