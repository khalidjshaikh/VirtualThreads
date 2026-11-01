/**
 * Virtual threads powering an HTTP server.
 *
 * Uses com.sun.net.httpserver.HttpServer with a virtual-thread-per-task
 * executor, so each request runs on its own virtual thread.
 */
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class VirtualThreadWebServer {
    public static void main(String[] args) throws Exception {
        int port = 8099;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/hello", ex -> {
            // Simulate blocking I/O
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            String body = "Hello from " + Thread.currentThread() + "\n";
            byte[] b = body.getBytes();
            ex.sendResponseHeaders(200, b.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(b); }
        });
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println("Server started on port " + port);

        // Fire a few concurrent requests
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                final int id = i;
                exec.submit(() -> {
                    try {
                        var url = new URI("http://localhost:" + port + "/hello").toURL();
                        try (var in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                            System.out.println("  req " + id + " -> " + in.readLine());
                        }
                    } catch (Exception e) {
                        System.out.println("  req " + id + " error: " + e);
                    }
                    return null;
                });
            }
        }
        Thread.sleep(200);
        server.stop(0);
        System.out.println("Server stopped. DONE");
    }
}