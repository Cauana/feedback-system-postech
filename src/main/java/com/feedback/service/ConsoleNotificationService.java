package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * ConsoleNotificationService - Implementação para desenvolvimento
 * Exibe notificações no console e registra métricas
 * Em produção, seria substituído por EmailNotificationService
 * DESABILITADO - Use EmailNotificationService
 */
// @ApplicationScoped
public class ConsoleNotificationService implements NotificationService {

    @Inject
    MetricsService metricsService;
    
    @Override
    public void notify(Feedback feedback) {
        System.out.println("\n┌─ ALERTA: FEEDBACK CRÍTICO ──┐");
        System.out.println("│ ID: " + feedback.id);
        System.out.println("│ Nota: " + feedback.nota + "/10");
        System.out.println("│ Descrição: " + feedback.descricao);
        System.out.println("│ Data: " + feedback.dataEnvio);
        System.out.println("└─────────────────────────────┘\n");

        // Registrar métrica
        if (metricsService != null) {
            metricsService.recordNotificationSent("CONSOLE", true);
        }
    }
    
    @Override
    public void notifyReport(Report report) {
        System.out.println("\n┌─ RELATÓRIO SEMANAL ──────────┐");
        System.out.println("│ Total: " + report.totalFeedbacks);
        System.out.println("│ Média: " + report.mediaNota + "/10");
        System.out.println("│ Críticos: " + report.feedbacksCriticos);
        System.out.println("│ Por Urgência: " + report.feedbacksPorUrgencia);
        System.out.println("│ Data: " + report.dataCriacao);
        System.out.println("└──────────────────────────────┘\n");

        // Registrar métrica
        if (metricsService != null) {
            metricsService.recordNotificationSent("CONSOLE", true);
        }
    }
}
