package br.com.alura.domain.saga;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Saga {

    public Saga() {

    }

    public Saga(String id, String entidade, SagaStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.entidade = entidade;
        this.status = status;
        this.createdAt = createdAt;
    }

    @Id
    public String id;

    public String entidade;

    @Enumerated(EnumType.STRING)
    public SagaStatus status;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public String getEntidade() {
        return entidade;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
