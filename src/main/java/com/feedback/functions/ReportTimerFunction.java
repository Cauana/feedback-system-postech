package com.feedback.functions;

import com.feedback.service.ReportService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import io.quarkus.arc.Arc;
import io.quarkus.funqy.Funq;

public class ReportTimerFunction {
    @Funq("ReportTimerFunction")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */1 * * * *")
            String timerInfo,
            final ExecutionContext context) {
        ReportService reportService = Arc.container().instance(ReportService.class).get();
        reportService.gerarRelatorioPeriodico();
        context.getLogger().info("Timer executado: " + timerInfo);
    }
}
