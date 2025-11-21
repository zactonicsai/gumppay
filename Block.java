import java.security.MessageDigest;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Block class representing a single block in the blockchain
 */
public class Block {
    private String hash;
    private String previousHash;
    private List<Transaction> transactions;
    private long timestamp;
    private int nonce;
    
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timestamp = new Date().getTime();
        this.nonce = 0;
        this.hash = calculateHash();
    }
    
    /**
     * Calculate the hash of this block using SHA-256
     */
    public String calculateHash() {
        try {
            String data = previousHash + 
                         Long.toString(timestamp) + 
                         Integer.toString(nonce) + 
                         transactionsToString();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Mine the block with proof-of-work (simplified for fast processing)
     */
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined: " + hash);
    }
    
    /**
     * Add a transaction to this block
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) return;
        
        // For genesis block, don't process transactions
        if (!"0".equals(previousHash)) {
            if (!transaction.isValid()) {
                System.out.println("Transaction is not valid. Discarded.");
                return;
            }
        }
        
        transactions.add(transaction);
    }
    
    /**
     * Convert all transactions to a string
     */
    private String transactionsToString() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(t.toString());
        }
        return sb.toString();
    }
    
    // Getters and setters
    public String getHash() {
        return hash;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int getNonce() {
        return nonce;
    }
}
