package com.feedback.resource;

import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @Inject
    FeedbackService service;

    @POST
    public Response create(Feedback feedback) {
        if (feedback == null || feedback.descricao == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Dados inválidos").build();
        }
        Feedback result = service.processar(feedback);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    public List<Feedback> list() {
        return service.listarTodos();
    }
    
    @GET
    @Path("/dashboard")
    public Map<String, Long> dashboard() {
        return Map.of(
            "total", service.contarTotal(),
            "criticos", service.contarCriticos()
        );
    }
}
