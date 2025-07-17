package io.github.vitalii.eshop.infra_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicConfigProperties {

    private int partitions;
    private short replicationFactor;

}
