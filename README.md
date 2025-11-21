# CrossPay - Blockchain-Based Cross-Border Payment System

## 🌍 Overview

CrossPay is a fast, low-cost blockchain application designed for international money transfers. Built with Java backend, Solidity smart contracts, and a modern HTML/JavaScript frontend with Tailwind CSS.

## ✨ Features

- **Fast Transactions**: Low difficulty mining for quick transaction processing (2-3 minutes)
- **Low Fees**: Only 0.1% transaction fee (minimum $0.01)
- **Multi-Currency Support**: USD, EUR, GBP, JPY, INR
- **Blockchain Transparency**: View complete transaction history and blockchain status
- **Secure**: SHA-256 hashing and proof-of-work validation
- **User-Friendly**: Modern, responsive web interface

## 🏗️ Architecture

### Components

1. **Solidity Smart Contract** (`CrossBorderPayment.sol`)
   - Handles payment logic on blockchain
   - Manages user balances and currency exchange
   - Event emission for transaction tracking

2. **Java Backend**
   - `Block.java`: Individual block implementation
   - `Transaction.java`: Payment transaction structure
   - `Blockchain.java`: Main blockchain management
   - `BlockchainServer.java`: REST API server

3. **Frontend** (`index.html`)
   - HTML5 + JavaScript
   - Tailwind CSS for styling
   - Real-time blockchain updates
   - Interactive payment interface

## 📋 Prerequisites

- Java JDK 11 or higher
- org.json library for JSON parsing
- Modern web browser
- (Optional) Solidity compiler for smart contract deployment

## 🚀 Installation

### Step 1: Download JSON Library

Download the JSON library JAR:
```bash
wget https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
```

Or add to Maven project:
```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20230227</version>
</dependency>
```

### Step 2: Compile Java Files

```bash
javac -cp json-20230227.jar:. Block.java Transaction.java Blockchain.java BlockchainServer.java
```

### Step 3: Run the Server

```bash
java -cp json-20230227.jar:. BlockchainServer
```

The server will start on `http://localhost:8080`

### Step 4: Open the Web Interface

Open `index.html` in your web browser, or navigate to:
```
http://localhost:8080
```

## 📖 Usage Guide

### Sending a Payment

1. Select your account (Alice or Bob)
2. Fill in recipient email
3. Enter amount and select currency
4. Specify sender and receiver countries
5. Click "Send Payment"
6. Transaction is added to pending pool

### Mining Transactions

1. Wait for pending transactions to accumulate
2. Click "Mine Block" button
3. Blockchain processes all pending transactions
4. Balances are updated automatically

### Viewing History

1. Click "Transaction History" to see all your transactions
2. View sent (red) and received (green) payments
3. See transaction fees and timestamps

### Depositing Funds

1. Click "Deposit Funds"
2. Enter amount to add to your account
3. Balance updates immediately

## 🔌 API Endpoints

### POST `/api/transaction/create`
Create a new payment transaction
```json
{
  "sender": "alice@email.com",
  "receiver": "bob@email.com",
  "amount": 100.0,
  "currency": "USD",
  "senderCountry": "United States",
  "receiverCountry": "United Kingdom"
}
```

### POST `/api/transaction/mine`
Mine pending transactions into a new block

### GET `/api/balance?address={email}`
Get account balance

### GET `/api/blockchain`
Get entire blockchain with all blocks and transactions

### GET `/api/validate`
Check if blockchain is valid

### POST `/api/deposit`
Deposit funds to account
```json
{
  "address": "alice@email.com",
  "amount": 1000.0
}
```

### GET `/api/pending`
Get all pending transactions

## 🎨 Design Principles

### Blockchain Design

- **Proof of Work**: Low difficulty (2) for fast processing
- **SHA-256 Hashing**: Industry-standard cryptographic security
- **Immutable Ledger**: All transactions permanently recorded
- **Chain Validation**: Continuous integrity checking

### Transaction Flow

1. User creates transaction → Added to pending pool
2. Miner processes pending transactions
3. New block created with proof-of-work
4. Block added to chain
5. Balances updated
6. Transaction marked complete

### Fee Structure

- Base fee: 0.1% of transaction amount
- Minimum fee: $0.01
- Mining reward: $0.01 per block
- All fees go to miners

## 🔐 Smart Contract Features

The Solidity smart contract includes:

- Balance management per user per currency
- Multi-currency support with exchange rates
- Payment initiation and completion
- Event logging for transparency
- Currency conversion utilities

### Deploying Smart Contract

```bash
solc --abi --bin CrossBorderPayment.sol -o build/
```

Use web3.js or similar to deploy to Ethereum testnet.

## 🧪 Testing

### Test Scenario 1: Basic Payment
```bash
# Alice sends $100 to Bob
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

# Mine the transaction
curl -X POST http://localhost:8080/api/transaction/mine

# Check Bob's balance
curl http://localhost:8080/api/balance?address=bob@email.com
```

## 📊 Performance

- **Block Mining Time**: ~100-500ms (difficulty 2)
- **Transaction Throughput**: Multiple transactions per block
- **API Response Time**: <50ms average
- **Frontend Load Time**: <2 seconds

## 🔮 Future Enhancements

- [ ] Multi-signature wallets
- [ ] Real-time exchange rate integration (Chainlink oracles)
- [ ] Mobile application
- [ ] Advanced analytics dashboard
- [ ] Network fee optimization
- [ ] Lightning Network-style payment channels
- [ ] Integration with real cryptocurrency networks
- [ ] KYC/AML compliance features
- [ ] Multi-language support

## 🛡️ Security Considerations

- **Hash Verification**: Every block and transaction is cryptographically verified
- **Chain Validation**: Continuous integrity checks prevent tampering
- **Low Attack Surface**: Simplified blockchain reduces vulnerabilities
- **Balance Verification**: Prevents double-spending

### Production Recommendations

1. Increase mining difficulty (4-6)
2. Implement proper user authentication
3. Add encryption for data transmission (HTTPS)
4. Use professional wallet management
5. Implement rate limiting
6. Add comprehensive logging
7. Deploy to secure hosting environment

## 🤝 Contributing

This is a demonstration project. For production use:

1. Implement proper security measures
2. Add comprehensive test suite
3. Use established blockchain frameworks
4. Implement proper key management
5. Add regulatory compliance features

## 📝 License

MIT License - Feel free to use and modify for learning purposes.

## ⚠️ Disclaimer

This is an educational demonstration of blockchain technology. Do NOT use for actual financial transactions without proper security audits, regulatory compliance, and production-grade infrastructure.

## 📧 Contact

For questions or suggestions about this demonstration project, please open an issue on the repository.

---

**Built with ❤️ for blockchain education and cross-border payment innovation**
