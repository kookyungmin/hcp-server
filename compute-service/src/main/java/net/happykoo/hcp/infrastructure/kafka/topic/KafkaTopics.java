package net.happykoo.hcp.infrastructure.kafka.topic;

public class KafkaTopics {

  public static final String INSTANCE_PROVISIONING_TOPIC = "hcp.compute.instance.provisioning";
  public static final String INSTANCE_PROVISIONING_DLT_TOPIC = "hcp.compute.instance.provisioning.DLT";
  public static final String INSTANCE_STATUS_TOPIC = "hcp.compute.instance.status";

  private KafkaTopics() {
  }

}
