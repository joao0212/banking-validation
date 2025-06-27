package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.messaging.InativarAgenciaProducer;
import br.com.alura.repository.SituacaoCadastralRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository situacaoCadastralRepository;
    private final InativarAgenciaProducer inativarAgenciaProducer;

    public SituacaoCadastralService(SituacaoCadastralRepository situacaoCadastralRepository, InativarAgenciaProducer inativarAgenciaProducer) {
        this.situacaoCadastralRepository = situacaoCadastralRepository;
        this.inativarAgenciaProducer = inativarAgenciaProducer;
    }

    @WithTransaction
    public Uni<Void> alterar(Agencia agencia) {
        return situacaoCadastralRepository
                .update("situacaoCadastral = ?1 where cnpj = ?2",
                        agencia.getSituacaoCadastral(), agencia.getCnpj())
                .call(() ->
                         inativarAgenciaProducer.enviarMensagemKafkaConfiguration(agencia)
                )
                .call(() ->
                        inativarAgenciaProducer.enviarMensagemSmallRyeMutinyEmiter(agencia)
                ).replaceWithVoid();
    }
}
