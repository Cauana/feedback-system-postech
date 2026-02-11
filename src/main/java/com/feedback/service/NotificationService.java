package com.feedback.service;

import com.feedback.model.Feedback;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "feedback.admin.email", defaultValue = "admin@example.com")
    String adminEmail;

    /**
     * Envia notificação de urgência para o administrador
     * quando a avaliação tem nota <= 3
     */
    public void enviarNotificacaoUrgencia(Feedback feedback) {
        String assunto = "[URGENTE] Feedback crítico recebido - Nota " + feedback.nota;
        
        String corpo = """
            <html>
            <body>
                <h2 style="color: #dc3545;">Alerta de Feedback Urgente</h2>
                <p>Um feedback crítico foi recebido e requer atenção imediata.</p>
                
                <table style="border-collapse: collapse; width: 100%%; max-width: 500px;">
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd; font-weight: bold;">Descrição:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd; font-weight: bold;">Nota:</td>
                        <td style="padding: 8px; border: 1px solid #ddd; color: #dc3545; font-weight: bold;">%d</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd; font-weight: bold;">Urgência:</td>
                        <td style="padding: 8px; border: 1px solid #ddd; color: #dc3545;">SIM</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd; font-weight: bold;">Data de Envio:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                    </tr>
                </table>
                
                <p style="margin-top: 20px; color: #666;">
                    Este é um e-mail automático do Sistema de Feedback.
                </p>
            </body>
            </html>
            """.formatted(
                feedback.descricao,
                feedback.nota,
                feedback.dataEnvio.format(FORMATTER)
            );

        try {
            mailer.send(
                Mail.withHtml(adminEmail, assunto, corpo)
            );
            LOG.info("Notificação de urgência enviada para: " + adminEmail);
        } catch (Exception e) {
            LOG.error("Erro ao enviar notificação de urgência: " + e.getMessage(), e);
        }
    }
}
