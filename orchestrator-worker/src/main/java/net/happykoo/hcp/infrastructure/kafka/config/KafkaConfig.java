package net.happykoo.hcp.infrastructure.kafka.config;

import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      ConsumerFactory<String, String> consumerFactory,
      DefaultErrorHandler errorHandler
  ) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
    factory.setConsumerFactory(consumerFactory);

    //성공했을 때만, ack 해서 commit
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

    factory.setCommonErrorHandler(errorHandler);

    return factory;
  }

  //DLQ 설정 (Dead Letter Queue) -> 5초 간격 3번 retry 해서 실패시 (원래 topic).DLT 로 메시지 produce
  @Bean
  public DefaultErrorHandler errorHandler(
      KafkaTemplate<String, String> template
  ) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        template,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
    );
    var backoff = new FixedBackOff(5000L, 3L);

    return new DefaultErrorHandler(recoverer, backoff);
  }

  @Bean
  public ProducerFactory<String, String> producerFactory(
      KafkaProperties kafkaProperties
  ) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties();

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(
      ProducerFactory<String, String> producerFactory
  ) {
    return new KafkaTemplate<>(producerFactory);
  }
}
