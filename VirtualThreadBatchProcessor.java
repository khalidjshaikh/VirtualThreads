/**
 * Batch processing with virtual threads.
 *
 * Processes a batch of items concurrently, collecting successes/failures.
 */
import java.util.*;
import java.util.concurrent.*;

public class VirtualThreadBatchProcessor {
    record Item(int id) {}
    record Result(int id, String value) {}

    static Result process(Item it) throws InterruptedException {
        Thread.sleep(20 + (it.id() % 5));
        if (it.id() % 7 == 0) throw new IllegalArgumentException("bad item " + it.id());
        return new Result(it.id(), "processed-" + it.id());
    }

    public static void main(String[] args) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) items.add(new Item(i));

        List<Result> ok = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Result>> futures = new ArrayList<>();
            for (Item it : items) futures.add(exec.submit(() -> process(it)));
            for (int i = 0; i < futures.size(); i++) {
                try {
                    ok.add(futures.get(i).get());
                } catch (Exception e) {
                    errors.add(e.getCause() != null ? e.getCause() : e);
                }
            }
        }
        System.out.println("processed ok=" + ok.size() + " errors=" + errors.size());
        System.out.println("first few: " + ok.subList(0, Math.min(5, ok.size())));
        System.out.println("DONE");
    }
}