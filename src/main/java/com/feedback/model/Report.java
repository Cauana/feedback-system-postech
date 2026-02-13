package com.feedback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;

/**
 * Report - Entidade para armazenar relatórios semanais de feedback
 * Persistência em PostgreSQL
 */
@Entity
public class Report extends PanacheEntity {

    public LocalDateTime dataCriacao;
    
    // Estatísticas principais
    public long totalFeedbacks;
    public long feedbacksCriticos;
    public double mediaNota;
    public long feedbacksPorUrgencia;
    
    // Detalhes em JSON/texto
    @Lob
    public String detalhesJson;
    
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
