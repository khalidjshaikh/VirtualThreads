/**
 * Virtual threads + CompletableFuture.
 *
 * Running blocking work on virtual threads instead of the common
 * ForkJoinPool avoids starving async pipelines.
 */
import java.util.*;
import java.util.concurrent.*;

public class VirtualThreadCompletableFuture {
    static String blockingCall(int id) throws InterruptedException {
        Thread.sleep(50);
        return "result-" + id;
    }

    public static void main(String[] args) throws Exception {
        ExecutorService vtExec = Executors.newVirtualThreadPerTaskExecutor();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int id = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                try { return blockingCall(id); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return null; }
            }, vtExec));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<String> f : futures) System.out.println("  " + f.get());
        vtExec.close();
        System.out.println("DONE");
    }
}