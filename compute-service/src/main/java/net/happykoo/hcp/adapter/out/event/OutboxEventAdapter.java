package net.happykoo.hcp.adapter.out.event;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.PublishOutboxEventPort;
import net.happykoo.hcp.common.annotation.EventOutAdapter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.infrastructure.properties.OutboxProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;

@EventOutAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(OutboxProperties.class)
public class OutboxEventAdapter implements PublishOutboxEventPort {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final OutboxProperties outboxProperties;

  @Override
  public void publishOutboxEvent(OutboxEvent outboxEvent)
      throws ExecutionException, InterruptedException, TimeoutException {
    ProducerRecord<String, String> record = new ProducerRecord<>(
        outboxEvent.getEventType().getTopic(),
        outboxEvent.getEventId().toString(),
        outboxEvent.getPayload());
    kafkaTemplate.send(record)
        .get(outboxProperties.kafkaAckTimeoutMs(), TimeUnit.MILLISECONDS);
  }
}
