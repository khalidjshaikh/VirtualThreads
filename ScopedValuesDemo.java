/**
 * ScopedValue (finalized in Java 25) - immutable, inheritable alternative to ThreadLocal.
 *
 * Scoped values are preferred with virtual threads because they are immutable,
 * cheaply inherited, and automatically cleaned up when the scope exits.
 */
import java.lang.ScopedValue;

public class ScopedValuesDemo {
    static final ScopedValue<String> USER = ScopedValue.newInstance();
    static final ScopedValue<Integer> REQUEST_ID = ScopedValue.newInstance();

    static void handleRequest(String user, int reqId) {
        ScopedValue.where(USER, user)
                   .where(REQUEST_ID, reqId)
                   .run(() -> {
                       System.out.println("  thread=" + Thread.currentThread() + " user=" + USER.get() + " req=" + REQUEST_ID.get());
                       doWork();
                   });
    }

    static void doWork() {
        // Scoped values are inherited by child threads forked within the scope.
        System.out.println("    doWork sees user=" + USER.get());
    }

    public static void main(String[] args) throws Exception {
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            exec.submit(() -> handleRequest("alice", 1));
            exec.submit(() -> handleRequest("bob", 2));
            // nested rebind
            ScopedValue.where(USER, "root").run(() -> {
                System.out.println("  nested root user=" + USER.get());
                ScopedValue.where(USER, "nested").run(() ->
                    System.out.println("  inner user=" + USER.get()));
                System.out.println("  back to root user=" + USER.get());
            });
        }
        Thread.sleep(200);
        System.out.println("DONE");
    }
}