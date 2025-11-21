import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

/**
 * Transaction class representing a cross-border payment
 */
public class Transaction {
    private String transactionId;
    private String sender;
    private String receiver;
    private double amount;
    private String currency;
    private String senderCountry;
    private String receiverCountry;
    private long timestamp;
    private String hash;
    private TransactionStatus status;
    
    public enum TransactionStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
    
    public Transaction(String sender, String receiver, double amount, 
                      String currency, String senderCountry, String receiverCountry) {
        this.transactionId = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.senderCountry = senderCountry;
        this.receiverCountry = receiverCountry;
        this.timestamp = new Date().getTime();
        this.status = TransactionStatus.PENDING;
        this.hash = calculateHash();
    }
    
    /**
     * Calculate the hash of this transaction
     */
    public String calculateHash() {
        try {
            String data = transactionId + sender + receiver + 
                         Double.toString(amount) + currency + 
                         Long.toString(timestamp);
            
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
     * Validate the transaction
     */
    public boolean isValid() {
        // Basic validation
        if (sender == null || receiver == null) return false;
        if (amount <= 0) return false;
        if (currency == null || currency.isEmpty()) return false;
        
        // Verify hash integrity
        return hash.equals(calculateHash());
    }
    
    /**
     * Calculate transaction fee (0.1% of amount, minimum 0.01)
     */
    public double calculateFee() {
        double fee = amount * 0.001; // 0.1% fee
        return Math.max(fee, 0.01); // Minimum fee of 0.01
    }
    
    @Override
    public String toString() {
        return transactionId + sender + receiver + amount + currency + timestamp;
    }
    
    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getReceiver() {
        return receiver;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public String getSenderCountry() {
        return senderCountry;
    }
    
    public String getReceiverCountry() {
        return receiverCountry;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getHash() {
        return hash;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
