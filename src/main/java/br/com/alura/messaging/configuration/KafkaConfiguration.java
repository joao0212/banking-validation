package br.com.alura.messaging.configuration;

import br.com.alura.Agencia;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import jakarta.enterprise.context.ApplicationScoped;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaConfiguration {

    private KafkaProducer<String, Agencia> producer;
    private final Vertx vertx;

    public KafkaConfiguration(Vertx vertx) {
        this.vertx = vertx;

        Map<String, String> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", "http://localhost:8081");

        producer = KafkaProducer.create(vertx, props);
    }

    public Uni<Void> enviarMensagem(String topic, Agencia value) {
        KafkaProducerRecord<String, Agencia> record = KafkaProducerRecord.create(topic, value);

        return Uni.createFrom().emitter(emmiter -> {
            producer.send(record, metadata -> {
                if (metadata.succeeded()) {
                    emmiter.complete(null);
                } else {
                    emmiter.fail(metadata.cause());
                }
            });
        });
    }
}
