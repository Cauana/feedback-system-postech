package com.feedback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Feedback extends PanacheEntity {

    public String descricao;
    public int nota;
    public boolean urgencia;
    public LocalDateTime dataEnvio;
    public String status; // NOVO, PROCESSADO, NOTIFICADO

    public Feedback() {}
}
