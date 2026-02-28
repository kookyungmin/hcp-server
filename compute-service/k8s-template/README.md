### Mac 환경 셋팅

ubuntu 22.04 기준 Compute Server 할당 테스트 명령

```
minikube start \
  --container-runtime=containerd \
  --cpus=4 \
  --memory=8192
minikube addons enable gvisor

docker build -t hcp_ubuntu:v1.0.0 . -f hcp_ubuntu.Dockerfile
minikube image load hcp_ubuntu:v1.0.0

# StorageClass
kubectl apply -f loopfs.yaml

kubectl apply -f test-compute-server.yaml
```
