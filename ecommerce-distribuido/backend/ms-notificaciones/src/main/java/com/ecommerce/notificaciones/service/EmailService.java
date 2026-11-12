package com.ecommerce.notificaciones.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service public class EmailService {
 private final JavaMailSender sender;
 public EmailService(JavaMailSender sender){this.sender=sender;}
 public void enviarPrueba(String destino){SimpleMailMessage m=new SimpleMailMessage();m.setFrom("demo@localhost");m.setTo(destino);m.setSubject("E-commerce: prueba");m.setText("Entorno MailHog de pruebas");sender.send(m);}
}
