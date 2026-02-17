package com.feedback.functions;

import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import io.quarkus.arc.Arc;
import io.quarkus.funqy.Funq;

import java.util.Optional;

public class FeedbackHttpFunction {
    @Funq("FeedbackHttpFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                         methods = {HttpMethod.POST},
                         route = "feedbacks",
                         authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            Optional<String> bodyOpt = request.getBody();
            if (bodyOpt.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Dados inválidos")
                        .build();
            }
            ObjectMapper mapper = new ObjectMapper();
            Feedback input = mapper.readValue(bodyOpt.get(), Feedback.class);

            FeedbackService service = Arc.container().instance(FeedbackService.class).get();
            Feedback result = service.processar(input);

            String json = mapper.writeValueAsString(result);
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(json)
                    .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar feedback")
                    .build();
        }
    }
}
