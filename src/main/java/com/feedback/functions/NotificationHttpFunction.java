package com.feedback.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.model.Feedback;
import com.feedback.service.NotificationService;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationHttpFunction {

    private static final Logger log = LoggerFactory.getLogger(NotificationHttpFunction.class);
    @Inject
    ObjectMapper mapper;

    @Inject
    NotificationService notificationService;

@FunctionName("NotificacaoHttpFunction")
    public void processarNotificacao(
            @QueueTrigger(
                    name = "msg",
                    queueName = "feedback-critico",
                    connection = "QUEUE_CONNECTION_STRING"
            ) String messageContent 
    ) {
        try {
            Feedback feedback = mapper.readValue(messageContent, Feedback.class);
            notificationService.notify(feedback);
        }catch (Exception ex){
            log.error("Erro ao processar mensagem da fila: " + ex.getMessage());
        }
    
    }
}
