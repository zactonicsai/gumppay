# 🚀 CrossPay Quick Start Guide

Get up and running with CrossPay blockchain in 5 minutes!

## Prerequisites

- Java JDK 11 or higher
- Modern web browser (Chrome, Firefox, Safari, Edge)
- Internet connection (for downloading dependencies)

## Quick Setup

### Option 1: Automated Setup (Recommended)

#### On Linux/Mac:
```bash
chmod +x build.sh
./build.sh
```

#### On Windows:
```cmd
build.bat
```

The script will:
1. Check Java installation
2. Download required libraries
3. Compile the code
4. Offer to start the server

### Option 2: Manual Setup

1. **Download JSON Library:**
   ```bash
   curl -L -o json-20230227.jar https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
   ```

2. **Compile Java Files:**
   
   Linux/Mac:
   ```bash
   javac -cp json-20230227.jar:. Block.java Transaction.java Blockchain.java BlockchainServer.java
   ```
   
   Windows:
   ```cmd
   javac -cp json-20230227.jar;. Block.java Transaction.java Blockchain.java BlockchainServer.java
   ```

3. **Start the Server:**
   
   Linux/Mac:
   ```bash
   java -cp json-20230227.jar:. BlockchainServer
   ```
   
   Windows:
   ```cmd
   java -cp json-20230227.jar;. BlockchainServer
   ```

4. **Open the Web Interface:**
   - Open `index.html` in your browser
   - Or navigate to `http://localhost:8080`

## First Transaction

1. **Select Account:** Choose Alice or Bob from the dropdown
2. **View Balance:** Click "Refresh" to see current balance
3. **Send Payment:**
   - Recipient: Enter `bob@email.com` (if you're Alice)
   - Amount: Enter `100`
   - Currency: Select `USD`
   - Countries: Fill in sender/receiver countries
   - Click "Send Payment"
4. **Mine Transaction:** Click "Mine Block" to process
5. **Check Results:** View updated balances and transaction history

## Test Scenarios

### Scenario 1: Simple Transfer
```
Alice → Bob
Amount: $100 USD
Fee: $0.10
Expected Time: < 1 minute
```

### Scenario 2: Multi-Currency
```
Bob → Alice
Amount: €50 EUR
Fee: €0.05
Expected Time: < 1 minute
```

### Scenario 3: Batch Processing
```
1. Create multiple transactions
2. Wait for them to queue (check "Pending Transactions")
3. Mine them all at once with "Mine Block"
4. All transactions processed in single block
```

## Default Accounts

| Account | Email | Initial Balance |
|---------|-------|-----------------|
| Alice | alice@email.com | $10,000 |
| Bob | bob@email.com | $5,000 |
| Miner | miner@system.com | $0 (receives fees) |

## Depositing More Funds

1. Click "Deposit Funds" button
2. Enter amount (e.g., 1000)
3. Funds added instantly to your account
4. Balance updates automatically

## Troubleshooting

### Server Won't Start

**Problem:** "Address already in use"
**Solution:** Port 8080 is occupied. Kill the process or change port in `BlockchainServer.java`

**Problem:** "JSON library not found"
**Solution:** Ensure `json-20230227.jar` is in the same directory

### Frontend Issues

**Problem:** "Cannot connect to server"
**Solution:** 
- Ensure server is running (`java -cp json-20230227.jar:. BlockchainServer`)
- Check console for errors
- Verify port 8080 is accessible

**Problem:** Transactions not appearing
**Solution:**
- Click "Refresh" to update balance
- Check browser console for errors
- Ensure JavaScript is enabled

### Compilation Errors

**Problem:** "Class not found"
**Solution:** Ensure all `.java` files are in the same directory

**Problem:** "Package org.json does not exist"
**Solution:** 
- Check `json-20230227.jar` is present
- Verify classpath in compile command

## API Testing with cURL

### Check Balance
```bash
curl http://localhost:8080/api/balance?address=alice@email.com
```

### Create Transaction
```bash
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
```

### Mine Transactions
```bash
curl -X POST http://localhost:8080/api/transaction/mine
```

### View Blockchain
```bash
curl http://localhost:8080/api/blockchain
```

## Performance Tips

1. **Mining Speed:** Difficulty is set to 2 for fast mining (~100-500ms per block)
2. **Batch Transactions:** Process multiple transactions in one block for efficiency
3. **Balance Caching:** Balances are cached in memory for instant retrieval

## Next Steps

- Read `README.md` for detailed documentation
- Check `DESIGN.md` for architecture details
- Review `CrossBorderPayment.sol` for smart contract implementation
- Explore the API endpoints
- Try creating your own custom transactions

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review error messages in browser console
3. Check server console for backend errors
4. Refer to full documentation in README.md

## Security Note

⚠️ **This is a demonstration project for educational purposes.**

Do NOT use for real financial transactions without:
- Proper security audits
- Production-grade infrastructure
- Regulatory compliance
- Professional cryptographic key management

---

**Happy Building! 🎉**

For more information, see the complete documentation in README.md and DESIGN.md
