package ProgettoINSW.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Recupero password - DietiEstate25");
        message.setText(
                "Hai richiesto il reset della password.\n\n" +
                        "Clicca il link qui sotto per impostarne una nuova:\n" +
                        resetLink + "\n\n" +
                        "Se non hai richiesto tu questa operazione, ignora questa email."
        );

        mailSender.send(message);
    }
}

