/**
 * ThreadLocal with virtual threads
 *
 * ThreadLocal works with virtual threads but each virtual thread gets its own
 * copy, which can be memory-heavy for millions of threads. Prefer ScopedValue
 * for inherited, read-mostly context. This demo shows ThreadLocal usage and
 * cleanup (remove) to avoid leaks when threads are pooled/reused.
 */
public class VirtualThreadThreadLocal {
    static final ThreadLocal<String> CTX = ThreadLocal.withInitial(() -> "default");

    public static void main(String[] args) throws Exception {
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                final int id = i;
                exec.submit(() -> {
                    CTX.set("req-" + id);
                    try {
                        System.out.println("  " + Thread.currentThread() + " ctx=" + CTX.get());
                    } finally {
                        CTX.remove(); // important for reused threads
                    }
                    return null;
                });
            }
        }
        System.out.println("main ctx=" + CTX.get()); // default
        System.out.println("DONE");
    }
}