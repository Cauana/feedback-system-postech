package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * EmailNotificationService - Implementação para produção
 * Envia notificações por email
 * Integrado com Azure SendGrid para envio em ambiente cloud
 */
@ApplicationScoped
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    
    @Inject
    Mailer mailer;

    @Inject
    MetricsService metricsService;
    
    @ConfigProperty(name = "notification.admin.email", defaultValue = "wellingtonfc@hotmail.com")
    String adminEmail;
    
    @ConfigProperty(name = "quarkus.mailer.from")
    Optional<String> emailFrom;
    
    @ConfigProperty(name = "quarkus.mailer.username")
    Optional<String> emailUser;
    
    @Override
    public void notify(Feedback feedback) {
        if (feedback.nota <= 3) {
            String subject = "ALERTA: Feedback Crítico Recebido";
            String body = criarCorpoNotificacaoCritica(feedback);
            
            boolean success = enviarEmail(adminEmail, subject, body);
            metricsService.recordNotificationSent("EMAIL", success);
            log.info("[notify] -> Email enviado com sucesso");
        }
    }
    
    @Override
    public void notifyReport(Report report) {
        String subject = "Relatório Semanal de Feedbacks - " + report.dataCriacao;
        String body = criarCorpoRelatorio(report);
        
        boolean success = enviarEmail(adminEmail, subject, body);
        metricsService.recordNotificationSent("EMAIL", success);
        log.info("[notifyReport] -> Email enviado com sucesso");
    }

    private boolean enviarEmail(String destinatario, String assunto, String corpo) {
        try {
            String to = destinatario == null ? "" : destinatario.trim();
            String from = emailFrom.filter(s -> !s.isBlank()).orElseGet(() -> emailUser.orElse("")).trim();
            
            Mail mail = new Mail()
                .addTo(to)
                .setSubject(assunto)
                .setHtml(corpo);
            
            if (!from.isBlank()) {
                mail.setFrom(from);
            }
            
            mailer.send(mail);
            log.info("[enviarEmail] -> Email enviado com sucesso para: " + to);
            return true;
        } catch (Exception e) {
            log.error("[enviarEmail] -> Erro ao enviar email: " + e.getMessage());
            metricsService.recordException(e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }
    
    private String criarCorpoNotificacaoCritica(Feedback feedback) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; }\n");
        html.append(".container { max-width: 600px; margin: 20px auto; }\n");
        html.append(".header { background-color: #d32f2f; color: white; padding: 20px; text-align: center; }\n");
        html.append(".content { padding: 20px; background-color: #f5f5f5; }\n");
        html.append(".field { margin: 10px 0; }\n");
        html.append(".label { font-weight: bold; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("<div class=\"header\">\n");
        html.append("<h2>ALERTA: Feedback Crítico</h2>\n");
        html.append("</div>\n");
        html.append("<div class=\"content\">\n");
        html.append("<div class=\"field\">\n");
        html.append("<span class=\"label\">ID do Feedback:</span>\n");
        html.append("<span>").append(feedback.id).append("</span>\n");
        html.append("</div>\n");
        html.append("<div class=\"field\">\n");
        html.append("<span class=\"label\">Nota Atribuida:</span>\n");
        html.append("<span style=\"color: red; font-weight: bold;\">").append(feedback.nota).append("/10</span>\n");
        html.append("</div>\n");
        html.append("<div class=\"field\">\n");
        html.append("<span class=\"label\">Descrição:</span>\n");
        html.append("<p>").append(feedback.descricao).append("</p>\n");
        html.append("</div>\n");
        html.append("<div class=\"field\">\n");
        html.append("<span class=\"label\">Data de Envio:</span>\n");
        html.append("<span>").append(feedback.dataEnvio).append("</span>\n");
        html.append("</div>\n");
        html.append("<hr>\n");
        html.append("<p><strong>Ação Necessária:</strong> Este feedback foi marcado como crítico e requer atenção imediata.</p>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }
    
    private String criarCorpoRelatorio(Report report) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; }\n");
        html.append(".container { max-width: 600px; margin: 20px auto; }\n");
        html.append(".header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }\n");
        html.append(".content { padding: 20px; background-color: #f5f5f5; }\n");
        html.append(".stats { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }\n");
        html.append(".stat-box { background: white; padding: 15px; border-radius: 8px; text-align: center; }\n");
        html.append(".stat-value { font-size: 24px; font-weight: bold; color: #1976d2; }\n");
        html.append(".stat-label { font-size: 12px; color: #666; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
        html.append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background-color: #1976d2; color: white; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("<div class=\"header\">\n");
        html.append("<h2>Relatório Semanal de Feedbacks</h2>\n");
        html.append("<p>Periodo: ").append(report.dataCriacao).append("</p>\n");
        html.append("</div>\n");
        html.append("<div class=\"content\">\n");
        html.append("<div class=\"stats\">\n");
        html.append("<div class=\"stat-box\">\n");
        html.append("<div class=\"stat-value\">").append(report.totalFeedbacks).append("</div>\n");
        html.append("<div class=\"stat-label\">Total de Feedbacks</div>\n");
        html.append("</div>\n");
        html.append("<div class=\"stat-box\">\n");
        html.append("<div class=\"stat-value\">").append(report.mediaNota).append("</div>\n");
        html.append("<div class=\"stat-label\">Média de Notas</div>\n");
        html.append("</div>\n");
        html.append("<div class=\"stat-box\">\n");
        html.append("<div class=\"stat-value\">").append(report.feedbacksCriticos).append("</div>\n");
        html.append("<div class=\"stat-label\">Feedbacks Criticos</div>\n");
        html.append("</div>\n");
        html.append("<div class=\"stat-box\">\n");
        html.append("<div class=\"stat-value\">").append(report.feedbacksPorUrgencia).append("</div>\n");
        html.append("<div class=\"stat-label\">Urgentes</div>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("<table>\n");
        html.append("<thead>\n");
        html.append("<tr>\n");
        html.append("<th>Data</th>\n");
        html.append("<th>Quantidade</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        html.append("<!-- Dados por dia seriam preenchidos dinamicamente -->\n");
        html.append("</tbody>\n");
        html.append("</table>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }
}
