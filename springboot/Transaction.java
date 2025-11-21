package com.crosspay.blockchain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

/**
 * Transaction model representing a cross-border payment
 */
@Data
@NoArgsConstructor
public class Transaction {
    
    private String transactionId;
    private String sender;
    private String receiver;
    private double amount;
    private String currency;
    private String senderCountry;
    private String receiverCountry;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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
     * Calculate the SHA-256 hash of this transaction
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
            throw new RuntimeException("Error calculating hash", e);
        }
    }
    
    /**
     * Validate the transaction
     */
    public boolean isValid() {
        if (sender == null || receiver == null) return false;
        if (amount <= 0) return false;
        if (currency == null || currency.isEmpty()) return false;
        return hash.equals(calculateHash());
    }
    
    /**
     * Calculate transaction fee (0.1% of amount, minimum 0.01)
     */
    public double calculateFee() {
        double fee = amount * 0.001;
        return Math.max(fee, 0.01);
    }
    
    @Override
    public String toString() {
        return transactionId + sender + receiver + amount + currency + timestamp;
    }
}
