package com.feedback.service;

import com.feedback.model.Feedback;

import java.time.LocalDateTime;

public class FeedbackService {

    public Feedback processar(Feedback feedback) {
        feedback.urgencia = feedback.nota <= 3;
        feedback.dataEnvio = LocalDateTime.now().toString();
        return feedback;
    }
}
