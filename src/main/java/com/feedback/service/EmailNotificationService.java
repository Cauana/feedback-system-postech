package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * EmailNotificationService - Implementação para produção
 * Envia notificações por email
 * Integrado com Azure SendGrid para envio em ambiente cloud
 */
@ApplicationScoped
public class EmailNotificationService implements NotificationService {
    
    @Inject
    Mailer mailer;
    
    private static final String EMAIL_ADMIN = "cauana.dias@hotmail.com";
    private static final String EMAIL_FROM = "noreply@feedback-system.com";
    
    @Override
    public void notify(Feedback feedback) {
        if (feedback.nota <= 3) {
            String subject = "ALERTA: Feedback Critico Recebido";
            String body = criarCorpoNotificacaoCritica(feedback);
            
            enviarEmail(EMAIL_ADMIN, subject, body);
        }
    }
    
    @Override
    public void notifyReport(Report report) {
        String subject = "Relatorio Semanal de Feedbacks - " + report.dataCriacao;
        String body = criarCorpoRelatorio(report);
        
        enviarEmail(EMAIL_ADMIN, subject, body);
    }
    
    private void enviarEmail(String destinatario, String assunto, String corpo) {
        try {
            Mail mail = new Mail()
                .setFrom(EMAIL_FROM)
                .addTo(destinatario)
                .setSubject(assunto)
                .setHtml(corpo);
            
            mailer.send(mail);
            System.out.println("Email enviado com sucesso para: " + destinatario);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            e.printStackTrace();
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
        html.append("<h2>ALERTA: Feedback Critico</h2>\n");
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
        html.append("<span class=\"label\">Descricao:</span>\n");
        html.append("<p>").append(feedback.descricao).append("</p>\n");
        html.append("</div>\n");
        html.append("<div class=\"field\">\n");
        html.append("<span class=\"label\">Data de Envio:</span>\n");
        html.append("<span>").append(feedback.dataEnvio).append("</span>\n");
        html.append("</div>\n");
        html.append("<hr>\n");
        html.append("<p><strong>Acao Necessaria:</strong> Este feedback foi marcado como critico e requer atencao imediata.</p>\n");
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
        html.append("<h2>Relatorio Semanal de Feedbacks</h2>\n");
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
        html.append("<div class=\"stat-label\">Media de Notas</div>\n");
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
