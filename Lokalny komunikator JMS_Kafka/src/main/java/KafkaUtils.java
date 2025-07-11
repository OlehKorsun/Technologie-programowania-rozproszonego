import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.*;

public class KafkaUtils {
    public static Set<String> getExistingTopics() {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
        ))) {
            return adminClient.listTopics().names().get();
        } catch (Exception e) {
            e.printStackTrace();
            return Set.of();
        }
    }
}
