package com.feedback.service;

import com.feedback.model.Feedback;
import com.feedback.model.Report;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportService - Responsabilidade: Gerar relatórios periódicos de feedbacks
 * Esta função serverless é acionada por timer semanal (TimerTrigger)
 */
@ApplicationScoped
public class ReportService {

    @Inject
    FeedbackService feedbackService;
    
    @Inject
    NotificationService notificationService;

    /**
     * Gera relatório semanal dos feedbacks
     * Agendado para rodar toda segunda-feira
     */
    @Scheduled(cron = "0 0 0 ? * MON") // Segunda-feira às 00:00
    @Transactional
    public void gerarRelatorioPeriodico() {
        try {
            // 1. Buscar feedbacks da última semana
            LocalDateTime umaSemanaAtras = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
            List<Feedback> feedbacksSemana = Feedback
                .find("dataEnvio >= ?1", umaSemanaAtras)
                .list();

            if (feedbacksSemana.isEmpty()) {
                System.out.println("Nenhum feedback na última semana");
                return;
            }

            // 2. Calcular estatísticas
            Map<String, Object> estatisticas = calcularEstatisticas(feedbacksSemana);
            
            // 3. Salvar relatório no BD
            Report relatorio = criarRelatorio(estatisticas, feedbacksSemana);
            
            // 4. Enviar notificação aos administradores
            notificarAdministradores(relatorio);
            
            System.out.println("✓ Relatório semanal gerado com sucesso!");
            System.out.println(formatarRelatorio(relatorio));
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcula estatísticas dos feedbacks
     */
    private Map<String, Object> calcularEstatisticas(List<Feedback> feedbacks) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total de feedbacks
        long total = feedbacks.size();
        stats.put("totalFeedbacks", total);
        
        // Feedbacks críticos (nota <= 3)
        long criticos = feedbacks.stream()
            .filter(f -> f.nota <= 3)
            .count();
        stats.put("feedbacksCriticos", criticos);
        
        // Média de nota
        double media = feedbacks.stream()
            .mapToInt(f -> f.nota)
            .average()
            .orElse(0.0);
        stats.put("mediaNota", Math.round(media * 100.0) / 100.0);
        
        // Contagem por dia
        Map<LocalDate, Long> porDia = new HashMap<>();
        feedbacks.forEach(f -> {
            LocalDate data = f.dataEnvio.toLocalDate();
            porDia.put(data, porDia.getOrDefault(data, 0L) + 1);
        });
        stats.put("porDia", porDia);
        
        // Contagem por urgência
        long porUrgencia = feedbacks.stream()
            .filter(f -> f.urgencia)
            .count();
        stats.put("porUrgencia", porUrgencia);
        
        return stats;
    }

    /**
     * Cria entidade Report para persistência
     */
    private Report criarRelatorio(Map<String, Object> stats, List<Feedback> feedbacks) {
        Report relatorio = new Report();
        relatorio.dataCriacao = LocalDateTime.now();
        relatorio.totalFeedbacks = (long) stats.get("totalFeedbacks");
        relatorio.feedbacksCriticos = (long) stats.get("feedbacksCriticos");
        relatorio.mediaNota = (double) stats.get("mediaNota");
        relatorio.feedbacksPorUrgencia = (long) stats.get("porUrgencia");
        
        // Serializar dados adicionais
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        Map<LocalDate, Long> porDia = (Map<LocalDate, Long>) stats.get("porDia");
        porDia.forEach((data, qtd) -> 
            sb.append(data).append(": ").append(qtd).append(" | ")
        );
        relatorio.detalhesJson = sb.toString();
        relatorio.status = "GERADO";
        
        relatorio.persist();
        return relatorio;
    }

    /**
     * Notifica administradores sobre o relatório
     */
    private void notificarAdministradores(Report relatorio) {
        notificationService.notifyReport(relatorio);
    }

    /**
     * Formata o relatório para exibição
     */
    private String formatarRelatorio(Report relatorio) {
        return String.format(
            "\n" +
            "╔══════════════════════════════════════╗\n" +
            "║     RELATÓRIO SEMANAL DE FEEDBACK    ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ Total de Feedbacks: %d\n" +
            "║ Média de Nota: %.2f/10\n" +
            "║ Feedbacks Críticos: %d\n" +
            "║ Por Urgência: %d\n" +
            "║ Detalhes: %s\n" +
            "╚══════════════════════════════════════╝\n",
            relatorio.totalFeedbacks,
            relatorio.mediaNota,
            relatorio.feedbacksCriticos,
            relatorio.feedbacksPorUrgencia,
            relatorio.detalhesJson
        );
    }
}
