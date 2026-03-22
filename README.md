# Happy Cloud Platform Server

<!-- prettier-ignore-start -->
![SpringBoot](https://shields.io/badge/springboot-black?logo=springboot&style=for-the-badge%22)
![Docker](https://shields.io/badge/docker-black?logo=docker&style=for-the-badge%22)
![Kafka](https://shields.io/badge/kafka-black?logo=apache-kafka&style=for-the-badge%22)
![Mysql](https://shields.io/badge/mysql-black?logo=mysql&style=for-the-badge%22)
![Redis](https://shields.io/badge/redis-black?logo=redis&style=for-the-badge%22)
![k8s](https://shields.io/badge/k8s-black?logo=kubernetes&style=for-the-badge%22)
![grafana](https://shields.io/badge/grafana-black?logo=grafana&style=for-the-badge%22)
![prometheus](https://shields.io/badge/prometheus-black?logo=prometheus&style=for-the-badge%22)
![Elasticsearch](https://shields.io/badge/elasticsearch-black?logo=elasticsearch&style=for-the-badge%22)
![Jaeger](https://shields.io/badge/jaeger-black?logo=jaeger&style=for-the-badge%22)

## 해피 클라우드 플랫폼 

Happy Cloud Platform은 Kubernetes 기반 클라우드 구조 모사 시스템

헥사고날 아키텍처 기반으로 Kubernetes 를 활용하여 Compute 및 Network 개념을 추상화하고 , 클라우드 서비스 동작 구조를 구현함

실제로는 Compute Server는 VM 으로 할당하고, Network 는 L2,L3 단에서 처리해야 하지만, 

본 프로젝트에서는 k8s 를 활용하여 최대한 클라우드 서비스를 흉내내는 것을 목표


![img.png](image/img.png)

### System Requirements

- [java] 21
- [springboot] 3.5.8
- [springcloud] 2025.0.1
- [mysql] 8.0.33
- [redis] 6.2.10
- [k8s] v1.35.1
- [kafka] 4.1.1


### Architecture

![img.png](img.png)


### 핵심 기능

✔ 로그인 인증 / 인가

* 아키텍처

![img_4.png](img_4.png)

* 관련 UI

![img_5.png](img_5.png)

✔ 인스턴스 라이프 사이클

* 아키텍처


* 관련 UI

![img_6.png](img_6.png)
![img_7.png](img_7.png)

* 인스턴스 생성
* 인스턴스 중지
* 인스턴스 재시작
* 인스턴스 삭제

✔ 웹 콘솔 연결

* 아키텍처

* 관련 UI

![img_12.png](img_12.png)

✔ Scale up / down

* 아키텍처

✔ SSH key 등록 / SSH 접근

* 아키텍처


* 관련 UI

![img_10.png](img_10.png)

![img_9.png](img_9.png)

✔ 보안 그룹 관리

* 아키텍처


* 관련 UI

![img_11.png](img_11.png) 

### Local Environment

* 관련 도구 실행

```
$ docker compose up -d
```

* env 설정

```
# env.template 작성 후
$ cp env.template .env.local
```

* kafka topic 생성

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
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.register.sshkey \
  --partitions 3 \
  --replication-factor 1;
  
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 \
  --create \
  --if-not-exists \
  --topic hcp.compute.instance.update.networkpolicy \
  --partitions 3 \
  --replication-factor 1;
  
```

* DB SQL 초기화

```
각 서비스 sql/init.sql 실행
```

* k8s 셋팅

minikube 실행 (Mac 에서만)

```
minikube start \
  --container-runtime=containerd \
  --cpus=4 \
  --memory=8192 \
  --cni=calico
minikube addons enable gvisor
minikube addons enable metallb
minikube tunnel
```

CNI 적용 (calico) - Minikube 에서는 안해도 됨

```
kubectl apply -f calico.yaml
```

22번 포트 기본 허용 제외

```
kubectl apply -f felixconfiguration.yaml
```

ubuntu 이미지 load

```
docker build -t hcp_ubuntu:v1.0.0 . -f hcp_ubuntu.Dockerfile
minikube image load hcp_ubuntu:v1.0.0
```

rocky 이미지 load

```
docker build -t hcp_rocky:v1.0.0 . -f hcp_rocky.Dockerfile
minikube image load hcp_rocky:v1.0.0
```

StorageClass

```
kubectl apply -f loopfs.yaml
kubectl apply -f compute-server-v1.yaml
```

* 각 서비스 실행

```
# 예시
$ java -jar user-service.jar --spring.profiles.active=local
```