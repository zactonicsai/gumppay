# 🐳 Docker Deployment Guide - CrossPay Blockchain

Complete guide to deploying CrossPay with Docker Compose, including frontend, backend, and Solidity contracts.

## 📋 Prerequisites

- Docker Engine 20.10+ 
- Docker Compose 2.0+
- 4GB RAM minimum
- 10GB free disk space

### Install Docker

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

**macOS/Windows:**
Download Docker Desktop from https://www.docker.com/products/docker-desktop

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                       │
│                   (crosspay-network)                    │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Frontend   │  │   Backend    │  │   Ganache    │ │
│  │    Nginx     │  │ Spring Boot  │  │  Ethereum    │ │
│  │   Port 80    │  │  Port 8080   │  │  Port 8545   │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
│         │                 │                 │          │
│         └─────────────────┴─────────────────┘          │
│                          │                              │
│                  ┌───────▼────────┐                     │
│                  │   Deployer     │                     │
│                  │   Truffle      │                     │
│                  └────────────────┘                     │
└─────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### 1. Build and Start All Services

```bash
# Clone or navigate to project directory
cd crosspay-blockchain

# Build and start all containers
docker-compose up -d --build

# View logs
docker-compose logs -f
```

### 2. Verify Services are Running

```bash
# Check status
docker-compose ps

# Should show:
# crosspay-frontend   - Up (port 80)
# crosspay-backend    - Up (port 8080)
# crosspay-ganache    - Up (port 8545)
# crosspay-deployer   - Up (contract deployment)
```

### 3. Access the Application

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/health
- **Ganache RPC**: http://localhost:8545

### 4. Stop Services

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## 📦 Services Overview

### Frontend (Nginx)
- **Image**: nginx:alpine
- **Port**: 80
- **Purpose**: Serves static HTML/CSS/JS
- **Features**: 
  - Automatic API proxying to backend
  - CORS headers configured
  - Health checks enabled

### Backend (Spring Boot)
- **Image**: eclipse-temurin:17
- **Port**: 8080
- **Purpose**: REST API for blockchain operations
- **Features**:
  - 8 RESTful endpoints
  - Auto-configured CORS
  - Health monitoring
  - Structured logging

### Ganache (Ethereum)
- **Image**: trufflesuite/ganache:latest
- **Port**: 8545
- **Purpose**: Local Ethereum blockchain
- **Features**:
  - 10 pre-funded accounts
  - 3-second block time
  - Persistent storage

### Contract Deployer (Truffle)
- **Image**: node:18-alpine
- **Purpose**: Compiles and deploys Solidity contracts
- **Features**:
  - Automatic compilation
  - Deployment to Ganache
  - Contract artifacts export

## 🔧 Configuration

### Environment Variables

Create `.env` file in project root:

```env
# Backend
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xms256m -Xmx512m

# Ganache
GANACHE_CHAIN_ID=1337
GANACHE_NETWORK_ID=1337
GANACHE_BLOCK_TIME=3
GANACHE_DEFAULT_BALANCE=10000

# Logging
LOG_LEVEL=INFO
```

### Custom Ports

Edit `docker-compose.yml` to change ports:

```yaml
services:
  frontend:
    ports:
      - "8000:80"  # Change 8000 to your desired port
  
  backend:
    ports:
      - "9090:8080"  # Change 9090 to your desired port
```

## 📊 Monitoring

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f ganache
docker-compose logs -f contract-deployer

# Last 100 lines
docker-compose logs --tail=100
```

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/health

# Frontend health
curl http://localhost/

# Ganache health
curl http://localhost:8545
```

### Container Stats

```bash
# Resource usage
docker stats

# Specific container
docker stats crosspay-backend
```

## 🔍 Troubleshooting

### Services Won't Start

**Problem**: Containers fail to start

**Solutions**:
```bash
# Check Docker daemon
sudo systemctl status docker

# Check logs for errors
docker-compose logs

# Rebuild without cache
docker-compose build --no-cache
docker-compose up -d
```

### Port Already in Use

**Problem**: "port is already allocated"

**Solutions**:
```bash
# Find process using port 80
sudo lsof -i :80
sudo lsof -i :8080

# Kill process
sudo kill -9 <PID>

# Or change ports in docker-compose.yml
```

### Backend Can't Connect to Ganache

**Problem**: Backend shows connection errors to Ganache

**Solutions**:
```bash
# Ensure Ganache is healthy
docker-compose ps

# Restart services in order
docker-compose restart ganache
docker-compose restart contract-deployer
docker-compose restart backend
```

### Contract Deployment Failed

**Problem**: Deployer shows compilation or deployment errors

**Solutions**:
```bash
# View deployer logs
docker-compose logs contract-deployer

# Restart deployment
docker-compose restart contract-deployer

# Check Ganache is running
curl http://localhost:8545
```

### Frontend Shows API Errors

**Problem**: Frontend can't connect to backend

**Solutions**:
```bash
# Check backend is running
curl http://localhost:8080/api/health

# Check nginx configuration
docker-compose exec frontend cat /etc/nginx/nginx.conf

# Restart frontend
docker-compose restart frontend
```

## 🔄 Updates and Rebuilding

### Update Code

```bash
# Pull latest changes
git pull

# Rebuild specific service
docker-compose build backend
docker-compose up -d backend

# Rebuild all services
docker-compose up -d --build
```

### Clear Everything

```bash
# Stop and remove containers, networks, volumes
docker-compose down -v

# Remove images
docker-compose down --rmi all

# Start fresh
docker-compose up -d --build
```

## 📈 Scaling

### Scale Backend

```bash
# Run multiple backend instances
docker-compose up -d --scale backend=3

# Note: Requires load balancer configuration
```

### Add Load Balancer

Create `docker-compose.override.yml`:

```yaml
version: '3.8'

services:
  load-balancer:
    image: nginx:alpine
    ports:
      - "8080:80"
    volumes:
      - ./lb-nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - backend
    networks:
      - crosspay-network
```

## 🔐 Security Best Practices

### Production Deployment

1. **Use secrets for sensitive data**:
```yaml
services:
  backend:
    secrets:
      - db_password
      - api_key

secrets:
  db_password:
    external: true
  api_key:
    external: true
```

2. **Enable HTTPS**:
```yaml
services:
  frontend:
    ports:
      - "443:443"
    volumes:
      - ./ssl:/etc/nginx/ssl
```

3. **Limit resources**:
```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

4. **Run as non-root**:
Already configured in Dockerfiles!

## 🧪 Testing

### Run Tests in Container

```bash
# Backend tests
docker-compose exec backend mvn test

# Contract tests
docker-compose exec contract-deployer truffle test
```

### API Testing

```bash
# Create transaction
curl -X POST http://localhost:8080/api/transaction/create \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "alice@email.com",
    "receiver": "bob@email.com",
    "amount": 100,
    "currency": "USD",
    "senderCountry": "USA",
    "receiverCountry": "UK"
  }'

# Mine transactions
curl -X POST http://localhost:8080/api/transaction/mine

# Check balance
curl http://localhost:8080/api/balance?address=alice@email.com
```

## 📦 Backup and Restore

### Backup Volumes

```bash
# Backup Ganache data
docker run --rm \
  -v crosspay-ganache-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/ganache-backup.tar.gz /data

# Backup contract artifacts
docker run --rm \
  -v crosspay-contract-artifacts:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/contracts-backup.tar.gz /data
```

### Restore Volumes

```bash
# Restore Ganache data
docker run --rm \
  -v crosspay-ganache-data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/ganache-backup.tar.gz -C /

# Restore contracts
docker run --rm \
  -v crosspay-contract-artifacts:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/contracts-backup.tar.gz -C /
```

## 🌐 Production Deployment

### Cloud Deployment (AWS, GCP, Azure)

1. **Set up Docker host**
2. **Install Docker and Docker Compose**
3. **Clone repository**
4. **Configure environment variables**
5. **Run with production profile**:

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Kubernetes Deployment

Convert to Kubernetes:

```bash
# Install kompose
curl -L https://github.com/kubernetes/kompose/releases/download/v1.28.0/kompose-linux-amd64 -o kompose
chmod +x kompose
sudo mv kompose /usr/local/bin/

# Convert docker-compose to Kubernetes
kompose convert

# Deploy to Kubernetes
kubectl apply -f .
```

## 📚 Additional Commands

```bash
# Enter container shell
docker-compose exec backend sh
docker-compose exec frontend sh

# Copy files from container
docker cp crosspay-backend:/app/logs/app.log ./

# View container details
docker inspect crosspay-backend

# Prune unused resources
docker system prune -a
```

## ✅ Verification Checklist

After deployment, verify:

- [ ] All 4 containers are running (`docker-compose ps`)
- [ ] Frontend accessible at http://localhost
- [ ] Backend health check passes at http://localhost:8080/api/health
- [ ] Ganache RPC responds at http://localhost:8545
- [ ] Contract deployed successfully (check deployer logs)
- [ ] Can create and mine transactions
- [ ] Balance updates correctly

## 🎉 Success!

Your CrossPay blockchain is now running in Docker! 

**Next Steps:**
- Test all API endpoints
- Create some transactions
- Mine blocks
- Explore the blockchain

---

**For help**: Check logs with `docker-compose logs -f`  
**Report issues**: Include output of `docker-compose ps` and `docker-compose logs`
