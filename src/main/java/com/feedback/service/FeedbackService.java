package com.feedback.service;

import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    
    @Inject
    NotificationService notificationService;

    @Inject
    QueueClient queueClient;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MetricsService metricsService;

    @Transactional
    public Feedback processar(Feedback feedback)  {
        long startTime = System.currentTimeMillis();
        
        try {
            feedback.dataEnvio = LocalDateTime.now();
            feedback.urgencia = feedback.nota <= 3;
            feedback.status = "PROCESSADO";
            feedback.persist();

            // Registrar métrica de feedback criado
            metricsService.recordFeedbackCreated(feedback.nota, feedback.urgencia);
            
            if (feedback.urgencia) {
                metricsService.recordCriticalFeedback();
            }

            try {
                String jsonFeedback = objectMapper.writeValueAsString(feedback);

                if (feedback.urgencia) {
                    queueClient.sendMessage(jsonFeedback);
                    log.info("[processar] -> Feedback crítico enviado para a fila com sucesso");
                    feedback.status = "NOTIFICADO";
                }
            } catch (JsonProcessingException e) {
                log.error("[processar] -> Erro ao converter feedback para JSON: " + e.getMessage());
                metricsService.recordException(e.getClass().getSimpleName());
                throw e;
            }

            long duration = System.currentTimeMillis() - startTime;
            metricsService.recordFeedbackProcessed(duration, "SUCCESS");

            return feedback;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            metricsService.recordFeedbackProcessed(duration, "ERROR");
            metricsService.recordException(e.getClass().getSimpleName());
            log.error("[processar] -> Erro ao processar feedback", e);
            throw new RuntimeException("Erro ao processar feedback", e);
        }
    }

    public List<Feedback> listarTodos() {
        log.info("[listarTodos] -> Listando todos os feedbacks. Total: " + Feedback.count());
        return Feedback.listAll();
    }

    public long contarTotal() {
        log.info("[contarTotal] -> Contando total de feedbacks. Total: " + Feedback.count());
        return Feedback.count();
    }

    public long contarCriticos() {
        log.info("[contarCriticos] -> Contando feedbacks críticos. Total: " + Feedback.count("urgencia", true));
        return Feedback.count("urgencia", true);
    }
}
