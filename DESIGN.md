# CrossPay System Design Document

## Executive Summary

CrossPay is a blockchain-based cross-border payment system designed to provide fast, low-cost international money transfers. This document outlines the technical architecture, design decisions, and implementation details.

## Table of Contents

1. System Architecture
2. Design Principles
3. Component Design
4. Data Flow
5. Security Considerations
6. Performance Optimization
7. Scalability Strategy

---

## 1. System Architecture

### 1.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend Layer                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  HTML/JavaScript/Tailwind CSS Web Interface          │  │
│  │  - User Authentication                                │  │
│  │  - Payment Form                                       │  │
│  │  - Transaction History                                │  │
│  │  - Blockchain Explorer                                │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Java REST API Server (BlockchainServer.java)        │  │
│  │  - Request Handling                                   │  │
│  │  - Business Logic                                     │  │
│  │  - API Endpoints                                      │  │
│  │  - JSON Serialization                                 │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Blockchain Layer                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Blockchain Core (Blockchain.java)                    │  │
│  │  - Chain Management                                   │  │
│  │  - Transaction Pool                                   │  │
│  │  - Mining Logic                                       │  │
│  │  - Balance Management                                 │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Block Structure (Block.java)                         │  │
│  │  - Hash Calculation                                   │  │
│  │  - Proof of Work                                      │  │
│  │  - Transaction Storage                                │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Transaction Model (Transaction.java)                 │  │
│  │  - Payment Details                                    │  │
│  │  - Validation Logic                                   │  │
│  │  - Fee Calculation                                    │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  Smart Contract Layer (Optional)            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Solidity Smart Contract                              │  │
│  │  - On-chain Payment Logic                             │  │
│  │  - Currency Exchange                                  │  │
│  │  - Event Emission                                     │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Technology Stack

**Frontend:**
- HTML5 for structure
- JavaScript (ES6+) for logic
- Tailwind CSS for styling
- Font Awesome for icons
- Fetch API for HTTP requests

**Backend:**
- Java 11+ for core logic
- com.sun.net.httpserver for REST API
- org.json for JSON handling
- SHA-256 for cryptographic hashing

**Smart Contract:**
- Solidity ^0.8.0
- EVM-compatible

---

## 2. Design Principles

### 2.1 Core Principles

**Speed First**
- Low mining difficulty (2) for fast block creation
- Minimal transaction validation overhead
- Optimized hash calculation

**Low Cost**
- 0.1% transaction fee (minimum $0.01)
- Efficient proof-of-work algorithm
- No intermediary fees

**Transparency**
- All transactions visible on blockchain
- Real-time balance updates
- Full transaction history access

**Simplicity**
- Clean, intuitive user interface
- Simple API design
- Minimal dependencies

### 2.2 Design Patterns Used

**Singleton Pattern**
- Single blockchain instance
- Centralized state management

**Factory Pattern**
- Block creation
- Transaction generation

**Observer Pattern**
- Balance updates trigger UI refresh
- Blockchain changes propagate to frontend

**REST API Pattern**
- Stateless communication
- Resource-based endpoints
- Standard HTTP methods

---

## 3. Component Design

### 3.1 Block Component

**Purpose:** Represents a single block in the blockchain

**Key Features:**
```java
- hash: SHA-256 hash of block contents
- previousHash: Link to previous block
- transactions: List of transactions in block
- timestamp: Block creation time
- nonce: Proof-of-work counter
```

**Mining Algorithm:**
```
1. Start with nonce = 0
2. Calculate hash = SHA256(previousHash + timestamp + nonce + transactions)
3. If hash starts with '00' (difficulty 2), block is mined
4. Else increment nonce and repeat
```

**Design Decisions:**
- Low difficulty ensures fast mining (~100-500ms)
- SHA-256 provides cryptographic security
- Timestamp ensures chronological ordering

### 3.2 Transaction Component

**Purpose:** Represents a cross-border payment

**Key Attributes:**
```java
- transactionId: UUID for unique identification
- sender/receiver: Email addresses (simplified)
- amount: Payment amount
- currency: USD, EUR, GBP, JPY, INR
- senderCountry/receiverCountry: Geographic info
- timestamp: Transaction creation time
- status: PENDING, PROCESSING, COMPLETED, FAILED
- hash: Transaction integrity verification
```

**Fee Structure:**
```
fee = max(amount * 0.001, 0.01)
```

**Validation Rules:**
1. Amount must be > 0
2. Sender must have sufficient balance
3. Currency must be supported
4. Hash must match calculated hash

### 3.3 Blockchain Component

**Purpose:** Manages the entire blockchain

**Core Responsibilities:**
1. Chain management (add blocks, validate)
2. Transaction pool handling
3. Mining coordination
4. Balance tracking

**State Management:**
```java
- chain: List<Block> - all blocks
- pendingTransactions: List<Transaction> - awaiting mining
- balances: Map<String, Double> - user balances
- difficulty: int - mining difficulty
```

**Key Operations:**

*createTransaction()*
- Validates transaction
- Checks sender balance
- Adds to pending pool

*minePendingTransactions()*
- Creates new block
- Adds all pending transactions
- Performs proof-of-work
- Updates balances
- Clears pending pool

*isChainValid()*
- Verifies all block hashes
- Checks block linkage
- Validates mining difficulty

### 3.4 API Server Component

**Purpose:** REST API for frontend communication

**Endpoints:**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | /api/transaction/create | Create new transaction |
| POST | /api/transaction/mine | Mine pending transactions |
| GET | /api/transaction/get | Get transaction by ID |
| GET | /api/balance | Get account balance |
| GET | /api/blockchain | Get full blockchain |
| GET | /api/validate | Validate blockchain |
| POST | /api/deposit | Deposit funds |
| GET | /api/pending | Get pending transactions |

**CORS Configuration:**
- Allows cross-origin requests
- Supports GET, POST, OPTIONS
- Content-Type: application/json

### 3.5 Smart Contract Design

**Purpose:** On-chain payment logic (Ethereum deployment)

**Key Functions:**

*deposit()*
- Add funds to user balance
- Emit BalanceDeposited event

*initiatePayment()*
- Deduct from sender balance
- Create payment record
- Emit PaymentInitiated event

*completePayment()*
- Add to receiver balance
- Mark payment complete
- Emit PaymentCompleted event

*convertCurrency()*
- Exchange rate calculation
- Multi-currency support

**Exchange Rates (USD equivalent):**
```solidity
USD: 100 (1.00)
EUR: 108 (1.08)
GBP: 127 (1.27)
JPY: 1 (0.01)
INR: 1 (0.01)
```

---

## 4. Data Flow

### 4.1 Payment Transaction Flow

```
1. User fills payment form
   ↓
2. Frontend sends POST to /api/transaction/create
   ↓
3. Server validates transaction
   ↓
4. Transaction added to pending pool
   ↓
5. Response sent to frontend with transaction ID
   ↓
6. User (or auto-miner) clicks "Mine Block"
   ↓
7. Frontend sends POST to /api/transaction/mine
   ↓
8. Server creates new block
   ↓
9. Proof-of-work performed
   ↓
10. Block added to chain
    ↓
11. Balances updated
    ↓
12. Frontend receives success response
    ↓
13. UI updates with new balances and history
```

### 4.2 Balance Query Flow

```
1. Frontend requests balance update
   ↓
2. GET /api/balance?address={email}
   ↓
3. Server looks up balance in memory
   ↓
4. JSON response with balance
   ↓
5. Frontend updates display
```

### 4.3 Blockchain Validation Flow

```
1. Periodic validation check
   ↓
2. Iterate through all blocks
   ↓
3. For each block:
   - Verify hash calculation
   - Check previous hash link
   - Validate mining difficulty
   ↓
4. Return validation status
```

---

## 5. Security Considerations

### 5.1 Cryptographic Security

**Hash Functions:**
- SHA-256 for all hashing
- Collision resistance
- Preimage resistance

**Proof of Work:**
- Prevents block tampering
- Computational cost for modification
- Chain immutability

### 5.2 Transaction Security

**Validation:**
- Balance verification before transaction
- Double-spend prevention
- Hash integrity checks

**Authentication (To Implement):**
- User authentication required
- Private key signing
- Session management

### 5.3 API Security

**Current Implementation:**
- CORS enabled for development
- Input validation
- Error handling

**Production Requirements:**
- HTTPS/TLS encryption
- API key authentication
- Rate limiting
- Input sanitization
- SQL injection prevention (if using DB)

### 5.4 Smart Contract Security

**Best Practices:**
- Reentrancy guards
- Overflow protection (Solidity 0.8+)
- Access control modifiers
- Event logging

---

## 6. Performance Optimization

### 6.1 Mining Optimization

**Strategy:**
- Low difficulty (2) for development
- Adjustable difficulty based on network size
- Parallel mining possibility

**Performance Metrics:**
- Block time: ~100-500ms
- Hash rate: ~2000-10000 hashes/second
- Transaction throughput: Multiple per block

### 6.2 API Performance

**Optimizations:**
- In-memory data storage
- Efficient JSON serialization
- Minimal computational overhead

**Response Times:**
- Balance query: <10ms
- Transaction creation: <50ms
- Block mining: 100-500ms
- Blockchain retrieval: <100ms

### 6.3 Frontend Performance

**Optimizations:**
- CDN for Tailwind CSS
- Minimal JavaScript
- Efficient DOM updates
- Auto-refresh at 10-second intervals

---

## 7. Scalability Strategy

### 7.1 Current Limitations

**Single Server:**
- All data in memory
- No persistence
- Single point of failure

**No Sharding:**
- All transactions in single chain
- Linear growth in storage

### 7.2 Scaling Solutions

**Horizontal Scaling:**
```
┌────────────┐
│  Frontend  │
└─────┬──────┘
      │
┌─────▼──────────────┐
│  Load Balancer     │
└──┬───────────┬─────┘
   │           │
┌──▼──┐     ┌──▼──┐
│Node1│     │Node2│
└──┬──┘     └──┬──┘
   │           │
┌──▼───────────▼──┐
│  Database       │
└─────────────────┘
```

**Database Integration:**
- PostgreSQL for transaction storage
- Redis for caching
- MongoDB for blockchain storage

**Sharding Strategy:**
- Geographic sharding (by country)
- Currency-based sharding
- Cross-shard transactions

**Layer 2 Solutions:**
- Payment channels
- State channels
- Rollups

### 7.3 Future Architecture

**Microservices:**
- Transaction service
- Mining service
- Balance service
- Notification service

**Message Queue:**
- RabbitMQ/Kafka for async processing
- Event-driven architecture
- Decoupled services

---

## 8. Testing Strategy

### 8.1 Unit Tests

**Components to Test:**
- Block hash calculation
- Transaction validation
- Balance updates
- Chain validation

### 8.2 Integration Tests

**Scenarios:**
- End-to-end payment flow
- Multi-user transactions
- Block mining process
- API endpoint functionality

### 8.3 Load Testing

**Metrics:**
- Concurrent users
- Transaction throughput
- API response times
- Mining performance

---

## 9. Deployment Strategy

### 9.1 Development Environment

**Current Setup:**
- Local server (localhost:8080)
- In-memory storage
- No authentication

### 9.2 Production Deployment

**Infrastructure:**
```
┌──────────────────┐
│   CloudFlare     │  CDN / DDoS Protection
└────────┬─────────┘
         │
┌────────▼─────────┐
│   Load Balancer  │  AWS ELB / Nginx
└────────┬─────────┘
         │
┌────────▼─────────┐
│  Application     │  EC2 / Docker
│  Servers (x3)    │  Auto-scaling
└────────┬─────────┘
         │
┌────────▼─────────┐
│  Database        │  RDS PostgreSQL
│  Master/Replica  │  Automated backups
└──────────────────┘
```

**Containerization:**
```dockerfile
FROM openjdk:11-jre-slim
COPY *.class /app/
COPY json-*.jar /app/
WORKDIR /app
CMD ["java", "-cp", "json-20230227.jar:.", "BlockchainServer"]
```

### 9.3 Monitoring

**Metrics:**
- Transaction volume
- Block creation rate
- API response times
- Error rates
- User activity

**Tools:**
- Prometheus for metrics
- Grafana for dashboards
- ELK stack for logging
- AlertManager for alerts

---

## 10. Regulatory Considerations

### 10.1 Compliance Requirements

**KYC (Know Your Customer):**
- Identity verification
- Document validation
- Risk assessment

**AML (Anti-Money Laundering):**
- Transaction monitoring
- Suspicious activity reporting
- Record keeping

**Data Protection:**
- GDPR compliance
- Data encryption
- Privacy policy

### 10.2 Financial Regulations

**Money Transmitter License:**
- State-by-state requirements
- Federal regulations
- International compliance

**Reporting:**
- Transaction reports
- Audit trails
- Tax documentation

---

## 11. Conclusion

CrossPay demonstrates a functional blockchain-based payment system with:
- Fast transaction processing
- Low fees
- Transparent operations
- Scalable architecture

The design prioritizes speed and simplicity while maintaining security and transparency. Future enhancements will focus on scalability, regulatory compliance, and production readiness.

---

## 12. Appendix

### 12.1 Glossary

- **Block**: Container for transactions in blockchain
- **Hash**: Cryptographic fingerprint
- **Nonce**: Number used once in mining
- **Proof of Work**: Mining algorithm
- **UTXO**: Unspent Transaction Output
- **Difficulty**: Mining complexity target

### 12.2 References

- Bitcoin Whitepaper: https://bitcoin.org/bitcoin.pdf
- Ethereum Yellow Paper: https://ethereum.github.io/yellowpaper/paper.pdf
- SHA-256 Specification: FIPS 180-4
- REST API Best Practices: RFC 2616

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Author:** System Architecture Team
