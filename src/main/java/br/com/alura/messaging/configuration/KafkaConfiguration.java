package br.com.alura.messaging.configuration;

import br.com.alura.Agencia;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaConfiguration {

    private KafkaProducer<String, Agencia> producer;
    private Vertx vertx;

    public KafkaConfiguration(Vertx vertx, Config config) {
        String kafkaHost = config.getValue("kafka.producer.host", String.class);
        this.vertx = vertx;

        Map<String, String> props = new HashMap<>();
        props.put("bootstrap.servers", kafkaHost);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", "http://localhost:8081");

        producer = KafkaProducer.create(vertx, props);
    }

    public Uni<Void> enviarMensagem(String topico, Agencia value) {
        KafkaProducerRecord<String, Agencia> record = KafkaProducerRecord.create(topico, value);

        return Uni.createFrom().emitter(emitter -> {
            producer.send(record, metadata -> {
                if (metadata.succeeded()) {
                    emitter.complete(null);
                } else {
                    emitter.fail(metadata.cause());
                }
            });
        });
    }
}
