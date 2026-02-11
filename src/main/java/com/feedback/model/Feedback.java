package com.feedback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback extends PanacheEntity {

    @Column(nullable = false, length = 1000)
    public String descricao;

    @Column(nullable = false)
    public int nota;

    @Column(nullable = false)
    public boolean urgencia;

    @Column(name = "data_envio", nullable = false)
    public LocalDateTime dataEnvio;
}
