import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * REST API Server for the Cross-Border Payment Blockchain
 */
public class BlockchainServer {
    private static Blockchain blockchain;
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        // Initialize blockchain
        blockchain = new Blockchain();
        
        // Create some test accounts with initial balances
        blockchain.deposit("alice@email.com", 10000.0);
        blockchain.deposit("bob@email.com", 5000.0);
        blockchain.deposit("miner@system.com", 0.0);
        
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Set up API endpoints
        server.createContext("/api/transaction/create", new CreateTransactionHandler());
        server.createContext("/api/transaction/mine", new MineTransactionHandler());
        server.createContext("/api/transaction/get", new GetTransactionHandler());
        server.createContext("/api/balance", new GetBalanceHandler());
        server.createContext("/api/blockchain", new GetBlockchainHandler());
        server.createContext("/api/validate", new ValidateChainHandler());
        server.createContext("/api/deposit", new DepositHandler());
        server.createContext("/api/pending", new GetPendingHandler());
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Blockchain server started on port " + PORT);
        System.out.println("Access the application at http://localhost:" + PORT);
    }
    
    /**
     * Handler for creating new transactions
     */
    static class CreateTransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    
                    Transaction transaction = new Transaction(
                        json.getString("sender"),
                        json.getString("receiver"),
                        json.getDouble("amount"),
                        json.getString("currency"),
                        json.getString("senderCountry"),
                        json.getString("receiverCountry")
                    );
                    
                    boolean success = blockchain.createTransaction(transaction);
                    
                    JSONObject response = new JSONObject();
                    response.put("success", success);
                    response.put("transactionId", transaction.getTransactionId());
                    response.put("message", success ? "Transaction created successfully" : "Transaction failed");
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error creating transaction: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for mining pending transactions
     */
    static class MineTransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    blockchain.minePendingTransactions("miner@system.com");
                    
                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("message", "Block mined successfully");
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error mining transactions: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for getting transaction details
     */
    static class GetTransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String transactionId = getQueryParam(query, "id");
                    
                    Transaction transaction = blockchain.getTransactionById(transactionId);
                    
                    if (transaction != null) {
                        JSONObject response = transactionToJSON(transaction);
                        sendResponse(exchange, 200, response.toString());
                    } else {
                        sendErrorResponse(exchange, "Transaction not found");
                    }
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error getting transaction: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for getting account balance
     */
    static class GetBalanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String address = getQueryParam(query, "address");
                    
                    double balance = blockchain.getBalance(address);
                    
                    JSONObject response = new JSONObject();
                    response.put("address", address);
                    response.put("balance", balance);
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error getting balance: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for getting entire blockchain
     */
    static class GetBlockchainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    JSONObject response = new JSONObject();
                    JSONArray blocks = new JSONArray();
                    
                    for (Block block : blockchain.getChain()) {
                        JSONObject blockJSON = new JSONObject();
                        blockJSON.put("hash", block.getHash());
                        blockJSON.put("previousHash", block.getPreviousHash());
                        blockJSON.put("timestamp", block.getTimestamp());
                        blockJSON.put("nonce", block.getNonce());
                        
                        JSONArray transactions = new JSONArray();
                        for (Transaction tx : block.getTransactions()) {
                            transactions.put(transactionToJSON(tx));
                        }
                        blockJSON.put("transactions", transactions);
                        
                        blocks.put(blockJSON);
                    }
                    
                    response.put("chain", blocks);
                    response.put("length", blockchain.getChain().size());
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error getting blockchain: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for validating the blockchain
     */
    static class ValidateChainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    boolean isValid = blockchain.isChainValid();
                    
                    JSONObject response = new JSONObject();
                    response.put("isValid", isValid);
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error validating chain: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for depositing funds
     */
    static class DepositHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    
                    String address = json.getString("address");
                    double amount = json.getDouble("amount");
                    
                    blockchain.deposit(address, amount);
                    
                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("newBalance", blockchain.getBalance(address));
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error depositing funds: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for getting pending transactions
     */
    static class GetPendingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    JSONArray pending = new JSONArray();
                    
                    for (Transaction tx : blockchain.getPendingTransactions()) {
                        pending.put(transactionToJSON(tx));
                    }
                    
                    JSONObject response = new JSONObject();
                    response.put("pending", pending);
                    response.put("count", pending.length());
                    
                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error getting pending transactions: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }
    
    /**
     * Serve static HTML file
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // This would serve the index.html file
            String response = "Please access index.html directly";
            sendResponse(exchange, 200, response);
        }
    }
    
private static void enableCORS(HttpExchange exchange) {
    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
}
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private static void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", message);
        sendResponse(exchange, 400, error.toString());
    }
    
    private static String getQueryParam(String query, String param) {
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(param)) {
                return keyValue[1];
            }
        }
        return null;
    }
    
    private static JSONObject transactionToJSON(Transaction tx) {
        JSONObject json = new JSONObject();
        json.put("transactionId", tx.getTransactionId());
        json.put("sender", tx.getSender());
        json.put("receiver", tx.getReceiver());
        json.put("amount", tx.getAmount());
        json.put("currency", tx.getCurrency());
        json.put("senderCountry", tx.getSenderCountry());
        json.put("receiverCountry", tx.getReceiverCountry());
        json.put("timestamp", tx.getTimestamp());
        json.put("status", tx.getStatus().toString());
        json.put("fee", tx.calculateFee());
        return json;
    }
}
