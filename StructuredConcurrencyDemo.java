/**
 * Structured Concurrency (preview API in Java 27)
 *
 * StructuredTaskScope lets a parent task fork subtasks and join them as a unit,
 * with cancellation propagation and clear lifetime management.
 *
 * Compile & run:
 *   javac --enable-preview --release 27 StructuredConcurrencyDemo.java
 *   java  --enable-preview StructuredConcurrencyDemo
 */
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ExecutionException;

public class StructuredConcurrencyDemo {

    static String fetchUser(int id) throws InterruptedException {
        Thread.sleep(80);
        return "user-" + id;
    }
    static int fetchOrders(String user) throws InterruptedException {
        Thread.sleep(60);
        return 3;
    }

    // 1. allSuccessfulOrThrow
    static void allSuccessful() throws Exception {
        System.out.println("== allSuccessfulOrThrow ==");
        try (var scope = StructuredTaskScope.open()) {
            var u = scope.fork(() -> fetchUser(1));
            var o = scope.fork(() -> fetchOrders("user-1"));
            scope.join();
            System.out.println("  result: " + u.get() + " has " + o.get() + " orders");
        }
    }

    // 2. anySuccessfulOrThrow
    static void anySuccessful() throws Exception {
        System.out.println("== anySuccessfulOrThrow ==");
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<String>anySuccessfulOrThrow())) {
            scope.fork(() -> { Thread.sleep(120); return "slow"; });
            scope.fork(() -> { Thread.sleep(30);  return "fast"; });
            String first = scope.join();
            System.out.println("  first success: " + first);
        }
    }

    // 3. awaitAllSuccessfulOrThrow with different types
    static void awaitAll() throws Exception {
        System.out.println("== awaitAllSuccessfulOrThrow ==");
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {
            StructuredTaskScope.Subtask<String> u = scope.fork(() -> fetchUser(2));
            StructuredTaskScope.Subtask<Integer> o = scope.fork(() -> fetchOrders("user-2"));
            scope.join();
            System.out.println("  " + u.get() + " -> " + o.get() + " orders");
        }
    }

    // 4. failure propagation / cancellation
    static void failure() {
        System.out.println("== failure propagation ==");
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> { Thread.sleep(200); return "late"; });
            scope.fork(() -> { throw new IllegalStateException("boom"); });
            scope.join();
        } catch (Exception e) {
            System.out.println("  caught: " + e.getClass().getSimpleName() + " cause=" + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    // 5. timeout
    static void timeout() {
        System.out.println("== timeout ==");
        try (var scope = StructuredTaskScope.open(
                cf -> cf.withTimeout(java.time.Duration.ofMillis(50)))) {
            scope.fork(() -> { Thread.sleep(200); return "too-slow"; });
            scope.join();
        } catch (Exception e) {
            System.out.println("  caught: " + e.getClass().getSimpleName() + " cause=" + (e.getCause() != null ? e.getCause().getClass().getSimpleName() : ""));
        }
    }

    public static void main(String[] args) throws Exception {
        allSuccessful();
        anySuccessful();
        awaitAll();
        failure();
        timeout();
        System.out.println("DONE");
    }
}