package com.feedback.functions;

import com.feedback.service.ReportService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportTimerFunction {

    private static final Logger log = LoggerFactory.getLogger(ReportTimerFunction.class);
    @Inject
    private ReportService reportService;

    @FunctionName("ReportTimerFunction")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "%REPORT_GENERATION_SCHEDULE%")
            String timerInfo,
            final ExecutionContext context) {
        reportService.gerarRelatorioPeriodico();
        context.getLogger().info("Timer executado: " + timerInfo);
        log.info("Relatório periódico gerado com sucesso.");
    }
}
