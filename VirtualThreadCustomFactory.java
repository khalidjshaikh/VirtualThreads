/**
 * Custom virtual thread factory with naming and uncaught exception handler.
 */
public class VirtualThreadCustomFactory {
    public static void main(String[] args) throws Exception {
        java.util.concurrent.ThreadFactory factory = Thread.ofVirtual()
                .name("worker-", 0)
                .uncaughtExceptionHandler((t, e) ->
                        System.out.println("  UNCAUGHT in " + t + ": " + e))
                .factory();

        Thread t = factory.newThread(() -> {
            System.out.println("  running " + Thread.currentThread());
            throw new RuntimeException("boom");
        });
        t.start();
        t.join();
        System.out.println("DONE");
    }
}