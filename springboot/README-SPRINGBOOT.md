# 🚀 CrossPay Blockchain - Spring Boot Edition

Fast, low-cost cross-border payment system built with Spring Boot, Docker, and Ethereum.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## ✨ Features

### Core Features
- 🔗 **Blockchain Technology**: SHA-256 hashing with proof-of-work
- 💱 **Multi-Currency**: USD, EUR, GBP, JPY, INR support
- ⚡ **Fast Transactions**: Low difficulty mining (~100-500ms per block)
- 💰 **Low Fees**: Only 0.1% transaction fee (minimum $0.01)
- 📊 **RESTful API**: 12 comprehensive endpoints
- 🎨 **Modern UI**: Responsive web interface with Tailwind CSS
- 🐳 **Docker Ready**: Full containerization with Docker Compose
- 📜 **Smart Contracts**: Solidity integration with Ganache

### Technical Features
- ✅ Spring Boot 3.2.0 with Java 17
- ✅ Lombok for reduced boilerplate
- ✅ Spring Validation for input validation
- ✅ Spring Actuator for health monitoring
- ✅ Web3j for Ethereum integration
- ✅ Multi-stage Docker builds
- ✅ Nginx reverse proxy
- ✅ Auto-deployment of Solidity contracts

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│          Frontend (Nginx)               │
│     HTML + JS + Tailwind CSS            │
│            Port 80                      │
└──────────────┬──────────────────────────┘
               │ HTTP/REST
┌──────────────▼──────────────────────────┐
│     Backend (Spring Boot)               │
│  ┌────────────────────────────────────┐ │
│  │    BlockchainController            │ │
│  │    (REST Endpoints)                │ │
│  └──────────┬─────────────────────────┘ │
│             │                            │
│  ┌──────────▼─────────────────────────┐ │
│  │    BlockchainService               │ │
│  │    (Business Logic)                │ │
│  └──────────┬─────────────────────────┘ │
│             │                            │
│  ┌──────────▼─────────────────────────┐ │
│  │  Block / Transaction Models        │ │
│  └────────────────────────────────────┘ │
│            Port 8080                    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Ganache (Ethereum)                 │
│   Local Blockchain + Smart Contracts    │
│            Port 8545                    │
└─────────────────────────────────────────┘
```

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Access application
open http://localhost
```

**That's it!** Frontend, backend, and blockchain are all running.

### Option 2: Local Development

**Prerequisites:**
- Java 17+
- Maven 3.8+
- Node.js 16+ (for Ganache)

```bash
# 1. Start Ganache
npm install -g ganache
ganache --port 8545

# 2. Start Spring Boot
mvn spring-boot:run

# 3. Open frontend
open index.html
```

## 📦 Project Structure

```
crosspay-blockchain/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── crosspay/
│       │           └── blockchain/
│       │               ├── CrossPayApplication.java
│       │               ├── controller/
│       │               │   └── BlockchainController.java
│       │               ├── service/
│       │               │   └── BlockchainService.java
│       │               ├── model/
│       │               │   ├── Block.java
│       │               │   └── Transaction.java
│       │               └── dto/
│       │                   └── TransactionDTO.java
│       └── resources/
│           └── application.yml
├── pom.xml
├── Dockerfile.backend
├── Dockerfile.frontend
├── Dockerfile.deployer
├── docker-compose.yml
├── nginx.conf
├── index.html
├── CrossBorderPayment.sol
├── truffle-config.js
├── migrations/
│   └── 1_deploy_contracts.js
└── deploy.sh
```

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api`

### Transaction Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transaction/create` | Create new transaction |
| POST | `/transaction/mine` | Mine pending transactions |
| GET | `/transaction/get?id={id}` | Get transaction by ID |
| GET | `/transactions?address={email}` | Get transactions for address |

### Balance Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/balance?address={email}` | Get account balance |
| POST | `/deposit` | Deposit funds to account |
| GET | `/balances` | Get all account balances |

### Blockchain Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/blockchain` | Get full blockchain |
| GET | `/validate` | Validate blockchain integrity |
| GET | `/pending` | Get pending transactions |
| GET | `/statistics` | Get blockchain statistics |
| GET | `/health` | Health check |

### Example Requests

**Create Transaction:**
```bash
curl -X POST http://localhost:8080/api/transaction/create \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "alice@email.com",
    "receiver": "bob@email.com",
    "amount": 100.0,
    "currency": "USD",
    "senderCountry": "United States",
    "receiverCountry": "United Kingdom"
  }'
```

**Mine Transactions:**
```bash
curl -X POST http://localhost:8080/api/transaction/mine
```

**Check Balance:**
```bash
curl http://localhost:8080/api/balance?address=alice@email.com
```

## 🎨 Frontend Usage

### Access the Application
- **Docker**: http://localhost
- **Local**: Open `index.html` in browser

### Features
1. **Account Selection**: Switch between Alice and Bob
2. **Send Payments**: Fill form and submit
3. **Mine Transactions**: Click "Mine Block" button
4. **View History**: See all past transactions
5. **Deposit Funds**: Add money to accounts
6. **Blockchain Explorer**: View all blocks

### Default Accounts
- **Alice**: `alice@email.com` (Balance: $10,000)
- **Bob**: `bob@email.com` (Balance: $5,000)
- **Miner**: `miner@system.com` (Receives fees)

## 🐳 Docker Deployment

### Services

**Frontend (Nginx)**
- Port: 80
- Purpose: Serves static files and proxies API

**Backend (Spring Boot)**
- Port: 8080
- Purpose: REST API and blockchain logic

**Ganache (Ethereum)**
- Port: 8545
- Purpose: Local blockchain for smart contracts

**Contract Deployer (Truffle)**
- Purpose: Compiles and deploys Solidity contracts

### Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f [service-name]

# Rebuild
docker-compose up -d --build

# Scale backend
docker-compose up -d --scale backend=3

# Clean everything
docker-compose down -v --rmi all
```

For detailed Docker guide, see [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)

## 🔧 Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: crosspay-blockchain

server:
  port: 8080

logging:
  level:
    com.crosspay.blockchain: DEBUG
```

### Docker Environment

Create `.env` file:

```env
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xms256m -Xmx512m
LOG_LEVEL=INFO
```

## 📊 Monitoring

### Health Checks

```bash
# Application health
curl http://localhost:8080/api/health

# Spring Actuator
curl http://localhost:8080/actuator/health

# All actuator endpoints
curl http://localhost:8080/actuator
```

### Metrics

```bash
# Get metrics
curl http://localhost:8080/actuator/metrics

# Blockchain statistics
curl http://localhost:8080/api/statistics
```

### Logs

```bash
# Docker logs
docker-compose logs -f backend

# Application logs (local)
tail -f logs/application.log
```

## 🧪 Testing

### Run Tests

```bash
# Maven
mvn test

# Docker
docker-compose exec backend mvn test
```

### API Testing

Use the provided `test-api.sh` script:

```bash
chmod +x test-api.sh
./test-api.sh
```

Or use Postman collection: `crosspay-api.postman_collection.json`

## 🔐 Security

### Current Implementation (Development)
- ✅ Input validation with Spring Validation
- ✅ CORS configured for localhost
- ✅ Health checks enabled
- ✅ Non-root Docker containers
- ✅ Exception handling

### Production Requirements
- ⚠️ Add Spring Security with JWT
- ⚠️ Enable HTTPS/TLS
- ⚠️ Implement rate limiting
- ⚠️ Add API key authentication
- ⚠️ Enhance input sanitization
- ⚠️ Set up security headers
- ⚠️ Regular security audits

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Docker
```bash
docker-compose up -d
```

### Cloud (AWS, GCP, Azure)
1. Build Docker image
2. Push to container registry
3. Deploy with orchestration (Kubernetes, ECS, etc.)
4. Configure load balancer
5. Set up monitoring

See [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) for details.

## 📈 Performance

### Benchmarks

| Operation | Time |
|-----------|------|
| Block Mining | 100-500ms |
| Transaction Creation | <10ms |
| Balance Query | <5ms |
| Blockchain Validation | <100ms |
| API Response | <50ms avg |

### Optimization Tips

1. **Adjust mining difficulty**: Change `DIFFICULTY` in `BlockchainService`
2. **Enable caching**: Add Spring Cache
3. **Database persistence**: Add JPA/Hibernate
4. **Connection pooling**: Configure HikariCP
5. **JVM tuning**: Adjust heap size

## 🔄 Development

### Adding New Endpoints

1. Add method to `BlockchainController`:
```java
@GetMapping("/custom")
public ResponseEntity<?> customEndpoint() {
    // Implementation
    return ResponseEntity.ok(result);
}
```

2. Restart application
3. Test endpoint

### Modifying Blockchain Logic

Edit `BlockchainService.java` and update:
- Mining difficulty
- Transaction fees
- Validation rules
- Balance management

## 📚 Documentation

- **[DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)** - Complete Docker guide
- **[DESIGN.md](DESIGN.md)** - Architecture details
- **[QUICKSTART.md](QUICKSTART.md)** - Quick start guide
- **[CORS_UPDATE.md](CORS_UPDATE.md)** - CORS configuration
- **[FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)** - React integration

## 🤝 Contributing

This is an educational project. Contributions welcome!

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## 📝 License

MIT License - see LICENSE file

## ⚠️ Disclaimer

**Educational Purpose Only**

This is a demonstration project for learning blockchain and Spring Boot. DO NOT use for real financial transactions without:

- Professional security audit
- Regulatory compliance
- Production infrastructure
- Proper key management
- Legal consultation

## 🎓 Learning Resources

- Spring Boot: https://spring.io/projects/spring-boot
- Docker: https://docs.docker.com/
- Blockchain: https://bitcoin.org/bitcoin.pdf
- Solidity: https://docs.soliditylang.org/
- Web3j: https://docs.web3j.io/

## 📧 Support

For issues or questions:
1. Check documentation
2. Review logs
3. Search existing issues
4. Create new issue with details

## 🎉 Acknowledgments

Built with:
- Spring Boot
- Docker
- Ganache
- Truffle
- Nginx
- Tailwind CSS

---

**Made with ❤️ for blockchain education and Spring Boot learning**

**Version**: 1.0.0-springboot  
**Last Updated**: November 2024
