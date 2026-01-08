package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.audit.Audit;
import br.com.alura.domain.saga.Saga;
import br.com.alura.domain.saga.SagaStatus;
import br.com.alura.repository.SagaRepository;
import br.com.alura.repository.SituacaoCadastralRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.LocalDateTime;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository situacaoCadastralRepository;

    private final Emitter<Audit> emitter;

    private final MutinyEmitter<br.com.alura.Agencia> kafkaEmitter;

    private final SagaRepository sagaRepository;

    private final ObjectMapper objectMapper;

    public SituacaoCadastralService(
            SituacaoCadastralRepository situacaoCadastralRepository,
            @Channel("notificacoes") Emitter<Audit> emitter,
            @Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> kafkaEmitter,
            SagaRepository sagaRepository
    ) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.emitter = emitter;
        this.kafkaEmitter = kafkaEmitter;
        this.sagaRepository = sagaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @WithTransaction
    public Uni<Void> alterar(Agencia agencia) {
        return situacaoCadastralRepository
                .update("situacaoCadastral = ?1 where cnpj = ?2",
                        agencia.getSituacaoCadastral(), agencia.getCnpj())
                .onItem()
                .invoke(() -> emitter.send(new Audit(agencia.getId(), agencia.getCnpj(), agencia.getSituacaoCadastral())))
                .call(() -> {
                    try {
                        br.com.alura.Agencia agenciaConvertida = new br.com.alura.Agencia(agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getSituacaoCadastral());
                        if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                            return sagaRepository.persist(new Saga(agencia.getCnpj(),
                                    objectMapper.writeValueAsString(agencia),
                                    SagaStatus.OPEN,
                                    LocalDateTime.now()
                            )).call(() -> kafkaEmitter.send(agenciaConvertida));
                        }
                        return Uni.createFrom().voidItem();
                    } catch (Exception e) {
                        return Uni.createFrom().failure(e);
                    }
                }).replaceWithVoid();
    }
}