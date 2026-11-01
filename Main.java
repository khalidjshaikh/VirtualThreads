/**
 * Entry point: run a demo by name.
 * Usage: java --enable-preview -cp out Main <DemoClass>
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Available demos:");
            String[] demos = {
                "VirtualThreadsIntro", "VirtualThreadExecutors", "VirtualThreadPerformance",
                "VirtualThreadPinning", "VirtualThreadThreadLocal", "ScopedValuesDemo",
                "StructuredConcurrencyDemo", "VirtualThreadSchedulerDemo",
                "VirtualThreadLockContention", "VirtualThreadBatchProcessor",
                "VirtualThreadWebServer", "VirtualThreadCustomFactory",
                "VirtualThreadCancellation", "VirtualThreadCompletableFuture"
            };
            for (String d : demos) System.out.println("  " + d);
            System.out.println("Run: java --enable-preview -cp out Main <DemoClass>");
            return;
        }
        Class<?> c = Class.forName(args[0]);
        c.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
    }
}