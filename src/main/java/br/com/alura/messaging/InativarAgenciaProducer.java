package br.com.alura.messaging;

import br.com.alura.domain.Agencia;
import br.com.alura.messaging.configuration.KafkaConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;

@ApplicationScoped
public class InativarAgenciaProducer {

    private final MutinyEmitter<br.com.alura.Agencia> emitter;
    private final ObjectMapper objectMapper;
    private final KafkaConfiguration kafkaConfiguration;

    public InativarAgenciaProducer(@Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> emitter, KafkaConfiguration kafkaConfiguration) {
        this.emitter = emitter;
        this.objectMapper = new ObjectMapper();
        this.kafkaConfiguration = kafkaConfiguration;
    }

    public Uni<Void> enviarMensagemKafkaConfiguration(Agencia agencia) {
        try {
            String agenciaConvertida = objectMapper.writeValueAsString(agencia);
            if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                return kafkaConfiguration.enviarMensagem("remover-agencia", "", agenciaConvertida);
            }
        } catch (JsonProcessingException e) {
            return Uni.createFrom().failure(e);
        }
        return Uni.createFrom().nullItem();
    }

    public Uni<Void> enviarMensagemSmallRyeMutinyEmiter(Agencia agencia) {
        try {
             //String agenciaConvertida = objectMapper.writeValueAsString(agencia);
            br.com.alura.Agencia agenciaConvertida =
                    new br.com.alura.Agencia(agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getSituacaoCadastral(), "Joao", "BB");
            if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                return emitter.send(agenciaConvertida);
            }
        } catch (Exception e) {
            return Uni.createFrom().failure(e);
        }
        return Uni.createFrom().nullItem();
    }
}
