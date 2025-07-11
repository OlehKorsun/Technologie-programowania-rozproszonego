import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class Demo {
    public static void main(String[] args) {
        EmbeddedKafkaBroker broker = new EmbeddedKafkaBroker(1)
                .kafkaPorts(9092);

        broker.afterPropertiesSet();

        javax.swing.SwingUtilities.invokeLater(LoginForm::new);
        javax.swing.SwingUtilities.invokeLater(LoginForm::new);
        javax.swing.SwingUtilities.invokeLater(LoginForm::new);
        javax.swing.SwingUtilities.invokeLater(LoginForm::new);

//        SwingUtilities.invokeLater(() -> new Chat("chat1", "Kinga"));
//        SwingUtilities.invokeLater(() -> new Chat("chat1", "Jakub"));
//        SwingUtilities.invokeLater(() -> new Chat("chat2", "Masha"));
//        SwingUtilities.invokeLater(() -> new Chat("chat2", "Misha"));


    }
}

class MessageProducer{
    private static KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(
            Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
            )
    );

    public static void send(ProducerRecord<String, String> producerRecord){
        kafkaProducer.send(producerRecord);
    }
}


class MessageConsumer{

    @Getter
    private final String id;

    @Getter
    @Setter
    private String topic;

    KafkaConsumer<String, String> kafkaConsumer;

    public MessageConsumer(String id, String topic){

        this.id = id;
        this.topic = topic;

        kafkaConsumer = new KafkaConsumer<String, String>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, id,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                )
        );
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(record -> {
            System.out.println(id + ": " + record.value());
        });
    }
}