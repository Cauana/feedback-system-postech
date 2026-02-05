package com.feedback.service;

import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackService {

    @Inject
    NotificationService notificationService;

    @Transactional
    public Feedback processar(Feedback feedback) {
        feedback.dataEnvio = LocalDateTime.now();
        feedback.urgencia = feedback.nota <= 3;
        feedback.status = "PROCESSADO";
        
        feedback.persist();
        
        if (feedback.urgencia) {
            notificationService.notify(feedback);
            feedback.status = "NOTIFICADO";
        }
        
        return feedback;
    }

    public List<Feedback> listarTodos() {
        return Feedback.listAll();
    }
    
    public long contarTotal() {
        return Feedback.count();
    }
    
    public long contarCriticos() {
        return Feedback.count("urgencia", true);
    }
}
