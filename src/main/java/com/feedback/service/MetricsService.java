package com.feedback.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Serviço para rastreamento de métricas customizadas.
 * Integra com Azure Application Insights via Micrometer.
 */
@ApplicationScoped
public class MetricsService {

    @Inject
    MeterRegistry meterRegistry;

    /**
     * Registra a criação de um feedback.
     * 
     * @param nota Nota do feedback (0-10)
     * @param urgente Se o feedback é urgente
     */
    public void recordFeedbackCreated(int nota, boolean urgente) {
        Counter.builder("feedback.created")
            .tag("nota", String.valueOf(nota))
            .tag("urgente", String.valueOf(urgente))
            .tag("urgencia", urgente ? "URGENTE" : "NORMAL")
            .description("Número de feedbacks criados")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Registra o tempo de processamento de um feedback.
     * 
     * @param durationMs Duração em milissegundos
     * @param status Status do processamento (SUCCESS, ERROR)
     */
    public void recordFeedbackProcessed(long durationMs, String status) {
        Timer.builder("feedback.processing.time")
            .tag("status", status)
            .description("Tempo de processamento de feedbacks")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Registra o envio de uma notificação.
     * 
     * @param type Tipo de notificação (EMAIL, SMS, CONSOLE)
     * @param success Se o envio foi bem-sucedido
     */
    public void recordNotificationSent(String type, boolean success) {
        Counter.builder("notification.sent")
            .tag("type", type)
            .tag("success", String.valueOf(success))
            .description("Notificações enviadas")
            .register(meterRegistry)
            .increment();

        if (!success) {
            Counter.builder("notification.failed")
                .tag("type", type)
                .description("Notificações falhadas")
                .register(meterRegistry)
                .increment();
        }
    }

    /**
     * Registra a geração de um relatório.
     * 
     * @param feedbackCount Número de feedbacks no relatório
     * @param durationMs Tempo para gerar o relatório
     */
    public void recordReportGenerated(int feedbackCount, long durationMs) {
        meterRegistry.gauge("report.feedback.count", feedbackCount);
        
        Timer.builder("report.generation.time")
            .description("Tempo para gerar relatórios")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
        
        Counter.builder("report.generated")
            .tag("feedback_count", String.valueOf(feedbackCount))
            .description("Relatórios gerados")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Registra o tempo de uma query no banco de dados.
     * 
     * @param query Nome da query (ex: "findById", "findAll")
     * @param durationMs Duração em milissegundos
     */
    public void recordDatabaseQuery(String query, long durationMs) {
        Timer.builder("database.query.time")
            .tag("query", query)
            .description("Tempo de execução de queries")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Registra uma falha de conexão com o banco de dados.
     */
    public void recordDatabaseError(String errorType) {
        Counter.builder("database.error")
            .tag("type", errorType)
            .description("Erros de banco de dados")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Registra uma exceção não tratada.
     * 
     * @param exceptionClass Classe da exceção
     */
    public void recordException(String exceptionClass) {
        Counter.builder("exception.occurred")
            .tag("exception_class", exceptionClass)
            .description("Exceções lançadas")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Registra feedback crítico (nota <= 3).
     */
    public void recordCriticalFeedback() {
        Counter.builder("feedback.critical")
            .description("Feedbacks críticos (nota <= 3)")
            .register(meterRegistry)
            .increment();
    }

    /**
     * Registra estatísticas gerais de feedbacks.
     * 
     * @param totalFeedbacks Total de feedbacks
     * @param averageRating Média de avaliação
     * @param criticalCount Número de críticos
     */
    public void recordFeedbackStatistics(long totalFeedbacks, double averageRating, long criticalCount) {
        meterRegistry.gauge("feedback.total", totalFeedbacks);
        meterRegistry.gauge("feedback.average.rating", averageRating);
        meterRegistry.gauge("feedback.critical.count", criticalCount);
    }
}
