package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.audit.Audit;
import br.com.alura.repository.SituacaoCadastralRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    public SituacaoCadastralService(
            SituacaoCadastralRepository situacaoCadastralRepository,
            @Channel("notificacoes") Emitter<Audit> emitter,
            @Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> kafkaEmitter
    ) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.emitter = emitter;
        this.kafkaEmitter = kafkaEmitter;
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
                            return kafkaEmitter.send(agenciaConvertida);
                        }
                        return Uni.createFrom().voidItem();
                    } catch (JsonProcessingException e) {
                        return Uni.createFrom().failure(e);
                    }
                }).replaceWithVoid();
    }
}