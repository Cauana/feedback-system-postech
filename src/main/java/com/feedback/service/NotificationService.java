package com.feedback.service;

import com.feedback.model.Feedback;

/**
 * NotificationService - Interface para enviar notificações
 * Implementações:
 * - Email (produção)
 * - Console (desenvolvimento)
 */
public interface NotificationService {

    /**
     * Envia notificação para feedback crítico
     */
    void notify(Feedback feedback);

    /**
     * Envia notificação de relatório semanal aos administradores
     */
    void notifyReport(Report report);
}
