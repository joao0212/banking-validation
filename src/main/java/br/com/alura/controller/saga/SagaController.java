package br.com.alura.controller.saga;

import br.com.alura.domain.saga.SagaStatus;
import br.com.alura.repository.SagaRepository;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/saga")
public class SagaController {

    private final SagaRepository sagaRepository;

    public SagaController(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    @PUT
    public Uni<Void> fecharSaga(String id) {
        return sagaRepository
                .update("status = ?1 where id = ?2", SagaStatus.COMPLETED, id)
                .replaceWithVoid();
    }
}
