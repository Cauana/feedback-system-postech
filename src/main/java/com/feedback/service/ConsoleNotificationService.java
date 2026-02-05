package com.feedback.service;

import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConsoleNotificationService implements NotificationService {
    @Override
    public void notify(Feedback feedback) {
        System.out.println("ALERTA: Feedback Crítico recebido! ID: " + feedback.id + " - Nota: " + feedback.nota);
    }
}
