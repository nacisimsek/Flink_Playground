# Kafka Kubernetes Configurations

## 🎯 **3-Node Cluster (ACTIVE)**
These files create the current 3-broker Kafka cluster:

- **`kafka-3-brokers.yaml`** - Individual Deployments for 3 Kafka brokers (kafka-0, kafka-1, kafka-2)
- **`kafka-pvcs-3node.yaml`** - Persistent Volume Claims for 3 brokers (kafka-pvc-0, kafka-pvc-1, kafka-pvc-2)  
- **`kraft-service-dual-3node.yaml`** - Headless service for inter-broker communication

### Deploy 3-Node Cluster:
```bash
kubectl apply -f kafka-pvcs-3node.yaml
kubectl apply -f kraft-service-dual-3node.yaml  
kubectl apply -f kafka-3-brokers.yaml
```

## 📦 **Single Node Setup (BACKUP)**
These files are kept for single-node deployment if needed:

- **`kraft-pvc.yaml`** - Single PVC for 1-node setup
- **`kraft-service-dual.yaml`** - Service for 1-node setup  
- **`kraft-statefulset-dual.yaml`** - StatefulSet for 1-node setup

### Deploy Single-Node Cluster:
```bash
kubectl apply -f kraft-pvc.yaml
kubectl apply -f kraft-service-dual.yaml
kubectl apply -f kraft-statefulset-dual.yaml
```

## 🔗 **Connect to Kafka**
Port forward to connect from local machine:
```bash
kubectl port-forward service/kafka-service 9092:9092 -n vvp
```

Then connect using: `localhost:9092`