package com.feedback.functions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import io.quarkus.arc.Arc;
import io.quarkus.funqy.Funq;
import jakarta.inject.Inject;

import java.util.Optional;

public class FeedbackHttpFunction {
    @Inject
    private FeedbackService feedbackService;

    @Inject
    private ObjectMapper mapper;

    @FunctionName("FeedbackHttpFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                         methods = {HttpMethod.POST},
                         route = "feedbacks",
                         authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Processando novo feedback.");
        try {
            String body = request.getBody().get();
            if (body.isBlank()){
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Dados inválidos")
                        .build();
            }
            Feedback input = mapper.readValue(body, Feedback.class);

            Feedback result = feedbackService.processar(input);

            String jsonResponse = mapper.writeValueAsString(result);

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
        } catch (JsonMappingException | JsonParseException e) {
            context.getLogger().severe("Erro de JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Erro no formato dos dados")
                    .build();
        } catch (Exception e) {
            // MUITO IMPORTANTE: Logar o stacktrace real para diagnóstico
            context.getLogger().severe("Erro inesperado: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage())
                    .build();
        }
    }
}