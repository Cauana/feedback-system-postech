package com.feedback.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.model.Feedback;
import com.feedback.service.NotificationService;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationQueueFunction {

    private static final Logger log = LoggerFactory.getLogger(NotificationQueueFunction.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    NotificationService notificationService;

@FunctionName("NotificationQueueFunction")
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
            log.info("[processarNotificacao] -> Notificação finalizada. Feedback descrição: " + feedback.descricao);
        }catch (Exception ex){
            log.error("[processarNotificacao] -> Erro ao processar mensagem da fila: " + ex.getMessage());
        }
    
    }
}
