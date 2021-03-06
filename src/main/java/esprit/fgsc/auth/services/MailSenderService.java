package esprit.fgsc.auth.services;

import esprit.fgsc.auth.model.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Slf4j
@Service
public class MailSenderService {
    private final JavaMailSender emailSender;
    private final static SimpleMailMessage message = new SimpleMailMessage();
    // TODO : SEND FROM HTML TEMPLATE
    @Autowired
    public MailSenderService(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }
    public void sendConfirmEmail(UserAccount user, String returnUrl) {
        message.setTo(user.getEmail());
        message.setSubject("Vodoo - Email confirmation");
        String url = returnUrl + user.getConfirmEmailToken();
        log.info("Confirm email url : {}", url);
        message.setText("Please confirm your email by following this url : <a href='"+ url + user.getConfirmEmailToken()+"'>Vodoo - Confirm password </a>");
        emailSender.send(message);
    }

    public void sendResetToken(UserAccount user, String frontReturnUrl) throws NullArgumentException, MailException {
        if(user.getPasswordResetToken() == null) throw new NullArgumentException("Empty token");
        message.setTo(user.getEmail());
        message.setSubject("Vodoo - Password reset link");
        String url = frontReturnUrl + user.getPasswordResetToken() + "/" + user.getEmail();
        log.info("Reset password url : {}",url);
        message.setText("<a href='"+ url + "'> Reset password </a>");
        emailSender.send(message);
    }

    public void sendPasswordChangedEmail(UserAccount user, String remoteAddr) {
        message.setTo(user.getEmail());
        message.setSubject("Vodoo - Password changed");
        message.setText("Hello "+ user.getName() + ", your password has been changed at "+user.getPasswordResetTime()+ " from the ip address :"+remoteAddr);
        log.info("IP ADDR : {}",remoteAddr);
        new Thread(() -> emailSender.send(message)).start();
    }

    private void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Attachments", file);
        emailSender.send(message);
    }
    public void sendEmailAsync(String userEmail, String subject, String content) {
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(content);
        new Thread(() -> emailSender.send(message)).start();
    }

}
