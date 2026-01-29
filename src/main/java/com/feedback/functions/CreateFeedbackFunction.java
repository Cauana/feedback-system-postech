package com.feedback.functions;

import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.Optional;

public class CreateFeedbackFunction {

    private final FeedbackService service = new FeedbackService();

    @FunctionName("createFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS
            )
            HttpRequestMessage<Optional<Feedback>> request,
            final ExecutionContext context
    ) {

        Feedback feedback = request.getBody().orElse(null);

        if (feedback == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Body inválido")
                    .build();
        }

        Feedback result = service.processar(feedback);

        context.getLogger().info("Feedback recebido: " + result.descricao);

        return request.createResponseBuilder(HttpStatus.CREATED)
                .body(result)
                .build();
    }
}
