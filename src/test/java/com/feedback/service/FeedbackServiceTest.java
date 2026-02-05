package com.feedback.service;

import com.feedback.model.Feedback;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FeedbackServiceTest {

    @Inject
    FeedbackService feedbackService;

    @Test
    void deveEmitirNotificacaoParaFeedbackCritico() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Feedback f = new Feedback();
            f.descricao = "Problema grave";
            f.nota = 2;

            feedbackService.processar(f);

            String output = outContent.toString();
            assertTrue(output.contains("ALERTA: Feedback Crítico recebido!"));
            assertTrue(output.contains("Nota: 2"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void naoDeveEmitirNotificacaoParaFeedbackNormal() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Feedback f = new Feedback();
            f.descricao = "Tudo ok";
            f.nota = 5;

            feedbackService.processar(f);

            String output = outContent.toString();
            assertFalse(output.contains("ALERTA: Feedback Crítico recebido!"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
