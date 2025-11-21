import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blockchain class managing the entire blockchain for cross-border payments
 */
public class Blockchain {
    private List<Block> chain;
    private List<Transaction> pendingTransactions;
    private int difficulty;
    private double miningReward;
    private Map<String, Double> balances; // User balances in USD equivalent
    
    public Blockchain() {
        this.chain = new ArrayList<>();
        this.pendingTransactions = new ArrayList<>();
        this.difficulty = 2; // Low difficulty for fast processing
        this.miningReward = 0.01;
        this.balances = new HashMap<>();
        
        // Create genesis block
        createGenesisBlock();
    }
    
    /**
     * Create the first block in the chain
     */
    private void createGenesisBlock() {
        Block genesis = new Block("0");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
    }
    
    /**
     * Get the latest block in the chain
     */
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    
    /**
     * Add a new transaction to pending transactions
     */
    public boolean createTransaction(Transaction transaction) {
        if (!transaction.isValid()) {
            return false;
        }
        
        // Check if sender has sufficient balance
        double senderBalance = getBalance(transaction.getSender());
        double requiredAmount = transaction.getAmount() + transaction.calculateFee();
        
        if (senderBalance < requiredAmount) {
            System.out.println("Insufficient balance");
            return false;
        }
        
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        pendingTransactions.add(transaction);
        return true;
    }
    
    /**
     * Mine pending transactions and create a new block
     */
    public void minePendingTransactions(String miningRewardAddress) {
        // Create new block with pending transactions
        Block block = new Block(getLatestBlock().getHash());
        
        // Add all pending transactions to the block
        for (Transaction transaction : pendingTransactions) {
            block.addTransaction(transaction);
            
            // Update balances
            double amount = transaction.getAmount();
            double fee = transaction.calculateFee();
            
            // Deduct from sender
            updateBalance(transaction.getSender(), -(amount + fee));
            
            // Add to receiver
            updateBalance(transaction.getReceiver(), amount);
            
            // Mining reward (fees go to miner)
            updateBalance(miningRewardAddress, fee + miningReward);
            
            // Update transaction status
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        }
        
        // Mine the block
        block.mineBlock(difficulty);
        
        // Add block to chain
        chain.add(block);
        
        // Clear pending transactions
        pendingTransactions.clear();
        
        System.out.println("Block mined successfully!");
    }
    
    /**
     * Get balance of an account
     */
    public double getBalance(String address) {
        return balances.getOrDefault(address, 0.0);
    }
    
    /**
     * Update balance of an account
     */
    private void updateBalance(String address, double amount) {
        double currentBalance = getBalance(address);
        balances.put(address, currentBalance + amount);
    }
    
    /**
     * Deposit funds to an account (simulate funding)
     */
    public void deposit(String address, double amount) {
        updateBalance(address, amount);
        System.out.println("Deposited " + amount + " to " + address);
    }
    
    /**
     * Validate the entire blockchain
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            // Verify current block hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal");
                return false;
            }
            
            // Verify previous block hash
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Previous hashes not equal");
                return false;
            }
            
            // Verify mining difficulty
            String target = new String(new char[difficulty]).replace('\0', '0');
            if (!currentBlock.getHash().substring(0, difficulty).equals(target)) {
                System.out.println("Block not properly mined");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get all transactions for a specific address
     */
    public List<Transaction> getTransactionsForAddress(String address) {
        List<Transaction> addressTransactions = new ArrayList<>();
        
        for (Block block : chain) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getSender().equals(address) || 
                    transaction.getReceiver().equals(address)) {
                    addressTransactions.add(transaction);
                }
            }
        }
        
        return addressTransactions;
    }
    
    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(String transactionId) {
        for (Block block : chain) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getTransactionId().equals(transactionId)) {
                    return transaction;
                }
            }
        }
        return null;
    }
    
    // Getters
    public List<Block> getChain() {
        return chain;
    }
    
    public List<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public Map<String, Double> getAllBalances() {
        return new HashMap<>(balances);
    }
}
