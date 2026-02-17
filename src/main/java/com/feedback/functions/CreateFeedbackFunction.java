package com.feedback.functions;

import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import io.quarkus.funqy.Funq;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

public class CreateFeedbackFunction {

    private static final Logger LOG = Logger.getLogger(CreateFeedbackFunction.class);

    @Inject
    FeedbackService service;

    @Funq("avaliacao")
    public Feedback run(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Body inválido");
        }

        Feedback result = service.processar(feedback);

        LOG.info("Feedback recebido: " + result.descricao);

        return result;
    }
}
