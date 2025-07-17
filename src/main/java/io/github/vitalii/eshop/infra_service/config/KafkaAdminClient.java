package io.github.vitalii.eshop.infra_service.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminClient {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.auth.enabled}")
    private boolean authEnabled;

    @Value("${kafka.auth.username:}")
    private String username;

    @Value("${kafka.auth.password:}")
    private String password;

    @Value("${kafka.clientProtocol:SASL_PLAINTEXT}")
    private String clientProtocol;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);
        config.put(AdminClientConfig.RETRIES_CONFIG, 5);
        config.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 3000);
        config.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 300_000);
        config.put(CommonClientConfigs.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG, 10_000);
        config.put(CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        config.put(CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10_000);

        if (authEnabled) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, clientProtocol);
            config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            config.put(
                    SaslConfigs.SASL_JAAS_CONFIG,
                    String.format(
                            "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                            username,
                            password
                    )
            );
        }

        return AdminClient.create(config);
    }

}
