package io.github.vitalii.eshop.infra_service.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicProperties {

    private List<String> topics;

}
