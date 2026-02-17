package com.feedback.repository;

import com.feedback.model.Feedback;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackRepository implements PanacheRepository<Feedback> {

    /**
     * Busca feedbacks dos últimos 7 dias
     */
    public List<Feedback> findLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return find("dataEnvio >= ?1 ORDER BY dataEnvio DESC", weekAgo).list();
    }

    /**
     * Conta feedbacks urgentes dos últimos 7 dias
     */
    public long countUrgentLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return count("urgencia = true AND dataEnvio >= ?1", weekAgo);
    }

    /**
     * Calcula média de notas dos últimos 7 dias
     */
    public Double averageNotaLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return getEntityManager()
                .createQuery("SELECT AVG(f.nota) FROM Feedback f WHERE f.dataEnvio >= :weekAgo", Double.class)
                .setParameter("weekAgo", weekAgo)
                .getSingleResult();
    }

    /**
     * Conta feedbacks por dia dos últimos 7 dias
     */
    public List<Object[]> countByDayLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return getEntityManager()
                .createQuery("SELECT CAST(f.dataEnvio AS LocalDate), COUNT(f) FROM Feedback f WHERE f.dataEnvio >= :weekAgo GROUP BY CAST(f.dataEnvio AS LocalDate) ORDER BY CAST(f.dataEnvio AS LocalDate)", Object[].class)
                .setParameter("weekAgo", weekAgo)
                .getResultList();
    }
}
