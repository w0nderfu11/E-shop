package io.github.vitalii.eshop.infra_service.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KafkaTopicInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicInitializer.class);

    private final AdminClient adminClient;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;

    @Override
    public void run(ApplicationArguments args) {
        List<String> topics = kafkaTopicProperties.getTopics();
        int partitions = kafkaTopicConfigProperties.getPartitions();
        short replicationFactor = kafkaTopicConfigProperties.getReplicationFactor();

        List<NewTopic> newTopics = topics.stream()
                .map(topic -> new NewTopic(topic, partitions, replicationFactor))
                .collect(Collectors.toList());

        try {
            CreateTopicsResult result = adminClient.createTopics(newTopics);

            result.values().forEach((topic, future) -> {
                try {
                    future.get();
                    log.info("Kafka topic created: {} (partitions={}, replicationFactor={})",
                            topic, partitions, replicationFactor);
                } catch (ExecutionException e) {
                    log.warn("Topic '{}' already exists or error: {}", topic, e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Thread interrupted while creating topic '{}'", topic);
                }
            });
        } catch (Exception e) {
            log.error("Error creating Kafka topics: {}", e.getMessage(), e);
        }
    }
}
