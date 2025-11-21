import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class CorsHandler implements HttpHandler {

    private final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost:3000",
        "http://localhost:8080"
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String origin = exchange.getRequestHeaders().getFirst("Origin");

        // 1. Set the Access-Control-Allow-Origin header
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
        } else {
            // If the origin is not allowed, you can choose to do nothing, 
            // which will cause the browser to block the request.
            // Or, you can set it to a specific value like '*' (less secure) 
            // or your own host (to avoid errors on local requests).
            // For security, we'll only set it for allowed origins.
        }
        
        // Always allow credentials for this example
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");

        // 2. Handle the Preflight OPTIONS request
        if (requestMethod.equalsIgnoreCase("OPTIONS")) {
            // Add headers required for the preflight response
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400"); // 24 hours cache for preflight

            // Send 204 No Content response for preflight
            exchange.sendResponseHeaders(204, -1); 
            return;
        }

        // --- Standard Request Handling (GET, POST, etc.) ---
        
        // Example: A simple GET response
        if (requestMethod.equalsIgnoreCase("GET")) {
            String response = "Hello from the server!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            // Handle other methods or return an error
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}