package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.audit.Audit;
import br.com.alura.repository.SituacaoCadastralRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository situacaoCadastralRepository;

    private final Emitter<Audit> emitter;

    public SituacaoCadastralService(SituacaoCadastralRepository situacaoCadastralRepository, @Channel("notificacoes") Emitter<Audit> emitter) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.emitter = emitter;
    }

    @WithTransaction
    public Uni<Void> alterar(Agencia agencia) {
        return situacaoCadastralRepository
                .update("situacaoCadastral = ?1 where cnpj = ?2",
                        agencia.getSituacaoCadastral(), agencia.getCnpj())
                .onItem().invoke(a -> {
                    // envio "fire-and-forget"
                    emitter.send(new Audit(agencia.getId(), agencia.getCnpj(), agencia.getSituacaoCadastral()));
                })
                .replaceWithVoid();
    }
}