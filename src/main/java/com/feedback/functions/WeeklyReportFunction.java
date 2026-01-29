package com.feedback.functions;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class WeeklyReportFunction {

    @FunctionName("weeklyReport")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 0 8 * * 1")
            String timerInfo,
            ExecutionContext context
    ) {
        context.getLogger().info("Gerando relatório semanal...");
    }
}
