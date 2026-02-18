package com.feedback.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback extends PanacheEntity {

    @Column(name = "descricao", nullable = false, length = 1000)
    public String descricao;

    @Column(name = "nota", nullable = false)
    public int nota;

    @Column(name = "urgencia", nullable = false)
    public boolean urgencia;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_envio", nullable = false)
    public LocalDateTime dataEnvio;

    @Column(name = "status")
    public String status; // NOVO, PROCESSADO, NOTIFICADO

    public Feedback() {}
}
