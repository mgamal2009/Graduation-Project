package com.backend.SafeSt.Service;

import com.backend.SafeSt.Util.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${server.url}")
    private String serverUrl;
    @Async
    public void sendEmail(String firstName, String to, String token) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Complete Registration!");
        String encryptedToken= Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        message.setText("Dear "+ firstName + ",\n" +
                "\n" +
                "Welcome to Safe St.\n\n" +
                "To activate your account and start using it, please confirm your email address by clicking on the below link. if clicking the link is not working, please copy and paste it in your browser.\n" +
                "\n" + serverUrl +"?urlToken=" + encryptedToken +
                "\n\nSincerely,\n\n" +
                "Safe St. Team");
        message.setFrom("safe.st.sec@gmail.com");
        mailSender.send(message);
    }

    public void sendEmergency(String firstName, String to, String longitude, String latitude, String trustedName, String category,String date ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Complete Registration!");
        message.setText("Dear "+ firstName + ",\n" +
                "\n" +
                "Your Trusted User " + trustedName +" faced an emergency (" + category + ")\n" +
                "His Location is (Longitude: " + longitude + ", Latitude: " + latitude + ")\n" +
                "Date: " + date + "\n\n" +
                "Please Help Contact Him\n" +
                "\n\nSincerely,\n\n" +
                "Safe St. Team");
        message.setFrom("safe.st.sec@gmail.com");
        mailSender.send(message);
    }
    /*@Async
    public void sendResetMail(String to, String token) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Password");
        String encryptedToken= Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        message.setText("To reset your password, please click here : "
                +serverUrl+"/update-password?urlToken=" +encryptedToken);
        message.setFrom("safe.st.sec@gmail.com");
        mailSender.send(message);
    }*/
}
