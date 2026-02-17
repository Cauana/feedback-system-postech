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

    @Transactional
    public Feedback processar(Feedback feedback)  {
        feedback.dataEnvio = LocalDateTime.now();
        feedback.urgencia = feedback.nota <= 3;
        feedback.status = "PROCESSADO";
        feedback.persist();

        try
        {
            String jsonFeedback = objectMapper.writeValueAsString(feedback);

            if (feedback.urgencia) {
                queueClient.sendMessage(jsonFeedback);
                log.info("Feedback critico enviado para a fila com sucesso");
                feedback.status = "NOTIFICADO";
            }

        }
        catch (JsonProcessingException e)
        {
            log.error("Erro ao converter feedback para JSON: " + e.getMessage());
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
