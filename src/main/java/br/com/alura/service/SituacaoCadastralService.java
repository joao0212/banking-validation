package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.audit.Audit;
import br.com.alura.repository.SituacaoCadastralRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository situacaoCadastralRepository;

    private final Emitter<Audit> emitter;

    private final MutinyEmitter<String> kafkaEmitter;

    private final ObjectMapper objectMapper;

    public SituacaoCadastralService(
            SituacaoCadastralRepository situacaoCadastralRepository,
            @Channel("notificacoes") Emitter<Audit> emitter,
            @Channel("remover-agencia-channel") MutinyEmitter<String> kafkaEmitter
    ) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.emitter = emitter;
        this.kafkaEmitter = kafkaEmitter;
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
                        return kafkaEmitter.send(objectMapper.writeValueAsString(agencia));
                    } catch (JsonProcessingException e) {
                        return Uni.createFrom().failure(e);
                    }
                }).replaceWithVoid();
    }
}