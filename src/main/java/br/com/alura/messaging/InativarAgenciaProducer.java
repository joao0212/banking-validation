package br.com.alura.messaging;

import br.com.alura.domain.Agencia;
import br.com.alura.messaging.configuration.KafkaConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;

@ApplicationScoped
public class InativarAgenciaProducer {

    private final ObjectMapper objectMapper;
    private final KafkaConfiguration kafkaConfiguration;
    private final MutinyEmitter<br.com.alura.Agencia> mutinyEmitter;

    public InativarAgenciaProducer(KafkaConfiguration kafkaConfiguration, @Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> mutinyEmitter) {
        this.objectMapper = new ObjectMapper();
        this.kafkaConfiguration = kafkaConfiguration;
        this.mutinyEmitter = mutinyEmitter;
    }

    public Uni<Void> enviarMensagemKafkaConfiguration(Agencia agencia) {
        try {
            if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                // String agenciaConvertida = objectMapper.writeValueAsString(agencia);
                br.com.alura.Agencia agenciaConvertida =
                        new br.com.alura.Agencia(
                                agencia.getNome(),
                                agencia.getRazaoSocial(),
                                agencia.getCnpj(), agencia.getSituacaoCadastral(), "Joao");
                return kafkaConfiguration.enviarMensagem("remover-agencia", agenciaConvertida);
            }
        } catch (Exception e) {
            return Uni.createFrom().failure(e);
        }
        return Uni.createFrom().nullItem();
    }

    public Uni<Void> enviarMensagemSmallRyeMutinyEmitter(Agencia agencia) {
        try {
            if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                //String agenciaConvertida = objectMapper.writeValueAsString(agencia);
                br.com.alura.Agencia agenciaConvertida =
                        new br.com.alura.Agencia(
                                agencia.getNome(),
                                agencia.getRazaoSocial(),
                                agencia.getCnpj(), agencia.getSituacaoCadastral(), "Joao");
                return mutinyEmitter.send(agenciaConvertida);
            }
        } catch (Exception e) {
            return Uni.createFrom().failure(e);
        }
        return Uni.createFrom().nullItem();
    }
}
