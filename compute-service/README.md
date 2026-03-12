### Kafka Topic 생성

```
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.provisioning \
  --partitions 3 \
  --replication-factor 1;
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.provisioning.DLT \
  --partitions 3 \
  --replication-factor 1;
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.status \
  --partitions 3 \
  --replication-factor 1;
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.update.lifecycle \
  --partitions 3 \
  --replication-factor 1;
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.scaling \
  --partitions 3 \
  --replication-factor 1;
  
```
