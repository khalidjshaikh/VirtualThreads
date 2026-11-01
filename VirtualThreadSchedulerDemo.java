/**
 * Virtual thread scheduler & carrier threads
 *
 * Virtual threads are mounted on "carrier" platform threads (a ForkJoinPool).
 * Thread.currentThread() returns the virtual thread; the carrier can be
 * observed via jdk trace or JFR. This demo prints thread info and uses
 * Thread.yield() as a scheduling hint.
 */
public class VirtualThreadSchedulerDemo {
    public static void main(String[] args) throws Exception {
        Thread v = Thread.ofVirtual().start(() -> {
            System.out.println("virtual thread: " + Thread.currentThread());
            System.out.println("  isVirtual: " + Thread.currentThread().isVirtual());
            Thread.yield();
            System.out.println("  after yield still on: " + Thread.currentThread());
        });
        v.join();

        // System properties controlling the default scheduler (read-only info)
        System.out.println("available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("jdk.virtualThreadScheduler.parallelism = " +
                System.getProperty("jdk.virtualThreadScheduler.parallelism"));
        System.out.println("jdk.virtualThreadScheduler.maxPoolSize = " +
                System.getProperty("jdk.virtualThreadScheduler.maxPoolSize"));
        System.out.println("DONE");
    }
}