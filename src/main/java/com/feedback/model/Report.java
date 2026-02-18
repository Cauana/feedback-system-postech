package com.feedback.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Report - Entidade para armazenar relatórios semanais de feedback
 * Persistência em PostgreSQL
 */
@Entity
@Table(name = "reports")
public class Report extends PanacheEntity {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_criacao", nullable = false)
    public LocalDateTime dataCriacao;
    
    // Estatísticas principais

    @Column(name = "total_feedbacks", nullable = false)
    public long totalFeedbacks;

    @Column(name = "feedbacks_criticos")
    public long feedbacksCriticos;

    @Column(name = "media_nota")
    public double mediaNota;

    @Column(name = "feedbacks_por_urgencia")
    public long feedbacksPorUrgencia;
    
    // Detalhes em JSON/texto
    @Column(name = "detalhes_json", columnDefinition = "Text")
    public String detalhesJson;

    @Column(name = "status")
    public String status; // GERADO, ENVIADO, ARQUIVADO
    
    public Report() {}

    @Override
    public String toString() {
        return "Report{" +
                "dataCriacao=" + dataCriacao +
                ", totalFeedbacks=" + totalFeedbacks +
                ", feedbacksCriticos=" + feedbacksCriticos +
                ", mediaNota=" + mediaNota +
                ", feedbacksPorUrgencia=" + feedbacksPorUrgencia +
                ", status='" + status + '\'' +
                '}';
    }
}
