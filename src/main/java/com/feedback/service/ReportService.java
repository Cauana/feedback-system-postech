package com.feedback.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReportService {

    @Inject
    FeedbackService feedbackService;

    @Scheduled(every = "1m") // Para teste local rápido, depois pode ser cron
    public void gerarRelatorioPeriodico() {
        long total = feedbackService.contarTotal();
        long criticos = feedbackService.contarCriticos();
        
        System.out.println("=== RELATÓRIO PERIÓDICO ===");
        System.out.println("Total de Feedbacks: " + total);
        System.out.println("Feedbacks Críticos: " + criticos);
        System.out.println("===========================");
    }
}
