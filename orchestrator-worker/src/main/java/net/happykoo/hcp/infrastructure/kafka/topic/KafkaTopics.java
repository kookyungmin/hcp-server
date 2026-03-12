package net.happykoo.hcp.infrastructure.kafka.topic;

public class KafkaTopics {

  public static final String INSTANCE_PROVISIONING_TOPIC = "hcp.compute.instance.provisioning";
  public static final String INSTANCE_UPDATE_LIFECYCLE_TOPIC = "hcp.compute.instance.update.lifecycle";
  public static final String INSTANCE_STATUS_TOPIC = "hcp.compute.instance.status";

  private KafkaTopics() {
  }

}
