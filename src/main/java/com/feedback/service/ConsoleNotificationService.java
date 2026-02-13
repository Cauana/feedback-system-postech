package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * ConsoleNotificationService - Implementação para desenvolvimento
 * Exibe notificações no console
 * Em produção, seria substituído por EmailNotificationService
 * DESABILITADO - Use EmailNotificationService
 */
// @ApplicationScoped
public class ConsoleNotificationService implements NotificationService {
    
    @Override
    public void notify(Feedback feedback) {
        System.out.println("\n┌─ ALERTA: FEEDBACK CRÍTICO ──┐");
        System.out.println("│ ID: " + feedback.id);
        System.out.println("│ Nota: " + feedback.nota + "/10");
        System.out.println("│ Descrição: " + feedback.descricao);
        System.out.println("│ Data: " + feedback.dataEnvio);
        System.out.println("└─────────────────────────────┘\n");
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
    }
}
