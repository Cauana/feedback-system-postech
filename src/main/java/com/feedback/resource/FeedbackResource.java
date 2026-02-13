package com.feedback.resource;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import com.feedback.service.FeedbackService;
import com.feedback.service.ReportService;
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
    
    @Inject
    ReportService reportService;

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
    
    /**
     * Endpoint para acessar relatórios semanais
     * GET /feedbacks/reports
     */
    @GET
    @Path("/reports")
    public List<Report> getReports() {
        return Report.findAll().list();
    }
    
    /**
     * Endpoint para acessar o último relatório gerado
     * GET /feedbacks/reports/latest
     */
    @GET
    @Path("/reports/latest")
    public Report getLatestReport() {
        Report latest = Report
            .find("ORDER BY dataCriacao DESC")
            .firstResult();
        
        if (latest == null) {
            throw new WebApplicationException("Nenhum relatório gerado ainda", Response.Status.NOT_FOUND);
        }
        return latest;
    }
    
    /**
     * Endpoint para gerar relatório manualmente (para testes)
     * POST /feedbacks/reports/generate
     */
    @POST
    @Path("/reports/generate")
    public Response generateReportManually() {
        try {
            reportService.gerarRelatorioPeriodico();
            return Response.ok("Relatório gerado com sucesso").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao gerar relatório: " + e.getMessage())
                .build();
        }
    }
}
