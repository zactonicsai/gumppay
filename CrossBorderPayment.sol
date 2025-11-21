// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * CrossBorderPayment Smart Contract
 * Handles fast, low-cost international money transfers
 */
contract CrossBorderPayment {
    
    // Struct to store payment details
    struct Payment {
        address sender;
        address receiver;
        uint256 amount;
        string currency;
        string senderCountry;
        string receiverCountry;
        uint256 timestamp;
        bool completed;
        string transactionId;
    }
    
    // Mapping of transaction ID to Payment
    mapping(string => Payment) public payments;
    
    // Array to store all transaction IDs
    string[] public transactionIds;
    
    // Mapping to track user balances in different currencies
    mapping(address => mapping(string => uint256)) public balances;
    
    // Exchange rates (simplified - in production, use oracle like Chainlink)
    mapping(string => uint256) public exchangeRates; // Rate to USD (multiplied by 100)
    
    // Events
    event PaymentInitiated(
        string indexed transactionId,
        address indexed sender,
        address indexed receiver,
        uint256 amount,
        string currency
    );
    
    event PaymentCompleted(
        string indexed transactionId,
        uint256 timestamp
    );
    
    event BalanceDeposited(
        address indexed user,
        uint256 amount,
        string currency
    );
    
    // Constructor - initialize exchange rates
    constructor() {
        exchangeRates["USD"] = 100;  // 1.00 USD
        exchangeRates["EUR"] = 108;  // 1.08 USD
        exchangeRates["GBP"] = 127;  // 1.27 USD
        exchangeRates["JPY"] = 1;    // 0.0067 USD (scaled)
        exchangeRates["INR"] = 1;    // 0.012 USD (scaled)
    }
    
    /**
     * Deposit funds into the contract
     */
    function deposit(string memory currency, uint256 amount) public payable {
        require(amount > 0, "Amount must be greater than 0");
        require(exchangeRates[currency] > 0, "Currency not supported");
        
        balances[msg.sender][currency] += amount;
        
        emit BalanceDeposited(msg.sender, amount, currency);
    }
    
    /**
     * Get user balance for a specific currency
     */
    function getBalance(address user, string memory currency) public view returns (uint256) {
        return balances[user][currency];
    }
    
    /**
     * Initiate a cross-border payment
     */
    function initiatePayment(
        string memory transactionId,
        address receiver,
        uint256 amount,
        string memory currency,
        string memory senderCountry,
        string memory receiverCountry
    ) public {
        require(bytes(transactionId).length > 0, "Transaction ID required");
        require(receiver != address(0), "Invalid receiver address");
        require(amount > 0, "Amount must be greater than 0");
        require(balances[msg.sender][currency] >= amount, "Insufficient balance");
        require(payments[transactionId].timestamp == 0, "Transaction ID already exists");
        
        // Deduct from sender's balance
        balances[msg.sender][currency] -= amount;
        
        // Create payment record
        payments[transactionId] = Payment({
            sender: msg.sender,
            receiver: receiver,
            amount: amount,
            currency: currency,
            senderCountry: senderCountry,
            receiverCountry: receiverCountry,
            timestamp: block.timestamp,
            completed: false,
            transactionId: transactionId
        });
        
        transactionIds.push(transactionId);
        
        emit PaymentInitiated(transactionId, msg.sender, receiver, amount, currency);
    }
    
    /**
     * Complete a payment (add to receiver's balance)
     */
    function completePayment(string memory transactionId) public {
        Payment storage payment = payments[transactionId];
        
        require(payment.timestamp > 0, "Payment does not exist");
        require(!payment.completed, "Payment already completed");
        require(msg.sender == payment.sender || msg.sender == payment.receiver, "Unauthorized");
        
        // Add to receiver's balance
        balances[payment.receiver][payment.currency] += payment.amount;
        payment.completed = true;
        
        emit PaymentCompleted(transactionId, block.timestamp);
    }
    
    /**
     * Get payment details
     */
    function getPayment(string memory transactionId) public view returns (
        address sender,
        address receiver,
        uint256 amount,
        string memory currency,
        string memory senderCountry,
        string memory receiverCountry,
        uint256 timestamp,
        bool completed
    ) {
        Payment memory payment = payments[transactionId];
        return (
            payment.sender,
            payment.receiver,
            payment.amount,
            payment.currency,
            payment.senderCountry,
            payment.receiverCountry,
            payment.timestamp,
            payment.completed
        );
    }
    
    /**
     * Get total number of transactions
     */
    function getTotalTransactions() public view returns (uint256) {
        return transactionIds.length;
    }
    
    /**
     * Convert amount between currencies
     */
    function convertCurrency(
        uint256 amount,
        string memory fromCurrency,
        string memory toCurrency
    ) public view returns (uint256) {
        require(exchangeRates[fromCurrency] > 0, "Source currency not supported");
        require(exchangeRates[toCurrency] > 0, "Target currency not supported");
        
        // Convert to USD then to target currency
        uint256 usdAmount = (amount * exchangeRates[fromCurrency]) / 100;
        uint256 targetAmount = (usdAmount * 100) / exchangeRates[toCurrency];
        
        return targetAmount;
    }
}
