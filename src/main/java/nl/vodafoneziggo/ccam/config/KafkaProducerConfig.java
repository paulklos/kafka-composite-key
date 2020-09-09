package nl.vodafoneziggo.ccam.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import nl.vodafoneziggo.ccam.event.RequestResponseEventKeyDto;
import nl.vodafoneziggo.ccam.event.RequestResponseEventValueDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Configuration for Kafka producers.
 */
@Configuration
public class KafkaProducerConfig {
    /**
     * A list of Kafka broker addresses to connect to.
     */
    @Value("${kafka.broker.addresslist}")
    private String brokerAddressList;

    /**
     * The url for the schema registry.
     */
    @Value("${kafka.schemaRegistry.url}")
    private String schemaRegistryUrl;

    /**
     * Create a producer factory.
     *
     * @return the factory
     */
    @Bean
    public ProducerFactory<RequestResponseEventKeyDto, RequestResponseEventValueDto> userEventProducerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddressList);
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        addClassProperty(props, KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        addClassProperty(props, VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Create a Kafka template.
     *
     * @param messageProducerFactory the producer factory
     * @return the template
     */
    @Bean
    public KafkaTemplate<RequestResponseEventKeyDto, RequestResponseEventValueDto> kafkaTemplate(
            final ProducerFactory<RequestResponseEventKeyDto, RequestResponseEventValueDto> messageProducerFactory) {
        return new KafkaTemplate<>(messageProducerFactory);
    }

    private void addClassProperty(final Map<String, Object> props, final String key, final Class<?> clazz) {
        props.put(key, clazz.getName());
    }
}
