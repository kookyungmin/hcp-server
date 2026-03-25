### 환경 셋팅

ubuntu 22.04 기준 Compute Server 할당 테스트 명령

minikube 실행 (Mac 에서만)

```
minikube start \
  --container-runtime=containerd \
  --cpus=4 \
  --memory=8192 \
  --cni=calico
minikube addons enable gvisor
minikube addons enable metallb
minikube addons enable ingress
minikube addons enable metrics-server
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

