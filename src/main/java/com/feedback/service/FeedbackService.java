package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.repository.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class FeedbackService {

    @Inject
    FeedbackRepository repository;

    @Inject
    NotificationService notificationService;

    @Transactional
    public Feedback processar(Feedback feedback) {
        feedback.urgencia = feedback.nota <= 3;
        feedback.dataEnvio = LocalDateTime.now();
        repository.persist(feedback);

        // Envia notificação se for urgente (nota <= 3)
        // if (feedback.urgencia) {
        //     notificationService.enviarNotificacaoUrgencia(feedback);
        // }

        return feedback;
    }
}
