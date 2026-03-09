package net.happykoo.hcp.adapter.out.event;

import com.google.gson.Gson;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.PublishInstanceStatusEventPort;
import net.happykoo.hcp.application.port.out.data.InstanceStatusData;
import net.happykoo.hcp.common.annotation.EventOutAdapter;
import net.happykoo.hcp.infrastructure.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

@EventOutAdapter
@RequiredArgsConstructor
public class InstanceStatusEventAdapter implements PublishInstanceStatusEventPort {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final Map<String, InstanceStatusData> instanceStatusDataMap = new ConcurrentHashMap<>();

  @Override
  public void publishInstanceStatusEvent(InstanceStatusData instanceStatusData) {
    if (isEqualsBeforeStatus(instanceStatusData)) {
      return;
    }
    instanceStatusDataMap.put(instanceStatusData.instanceId(), instanceStatusData);
    ProducerRecord<String, String> record = new ProducerRecord<>(
        KafkaTopics.INSTANCE_STATUS_TOPIC,
        new Gson().toJson(instanceStatusData)
    );
    kafkaTemplate.send(record);
  }

  private boolean isEqualsBeforeStatus(InstanceStatusData instanceStatusData) {
    return instanceStatusDataMap.containsKey(instanceStatusData.instanceId())
        && instanceStatusDataMap.get(instanceStatusData.instanceId()).equals(instanceStatusData);
  }
}
