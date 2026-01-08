package br.com.alura.repository;

import br.com.alura.domain.saga.Saga;
import br.com.alura.domain.saga.SagaStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SagaRepository implements PanacheRepository<Saga> {

    @WithSession
    public Uni<List<Saga>> listByStatusAndCreatedAt(LocalDateTime createdAt) {
        return list("createdAt < ?1 and status = ?2", createdAt, SagaStatus.OPEN);
    }
}
