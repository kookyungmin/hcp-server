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

## 해피 클라우드 플랫폼 서버

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


### Use case (MVP)

- [x] 인증 / 인가
- [ ] Compute Server Resource (OS Image) 관리
- [ ] 사용량(Resource 시간 당 사용량, Traffic 용량) 및 과금 관리
- [ ] 사용자 요금 정산 / 결제
- [ ] 방화벽(보안 그룹) 관리
- [ ] IAM (서브 계정, 권한(RBAC)) 관리
- [ ] Scale Up / Down
- [ ] Observability

### Use case (Advance)
- [ ] Region 관리
- [ ] 회원 관리 (회원 정보 관리, 회원가입, 아이디/비밀번호 찾기, 비밀번호 변경)
- [ ] VPC 관리
- [ ] VM 할당으로 변경 (Windows Image 등 제공)
- [ ] Serverless Functions (Runtime Runner, API Generator)
- [ ] Object Storage
- [ ] 관리자 사이트 (사용자 통계, 사용량 / 과금 통계, 시스템 관리)
- [ ] (DB, Redis) Cluster / Master, Slaves / Backup
- [ ] AutoScale
- [ ] OAuth2.0 연동

