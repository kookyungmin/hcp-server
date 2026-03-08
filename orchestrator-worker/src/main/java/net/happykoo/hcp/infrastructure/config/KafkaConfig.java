package net.happykoo.hcp.infrastructure.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
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

    //3초 간격으로 5번 재시도
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
}
