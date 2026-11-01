/**
 * Virtual Threads (Project Loom) - Basics
 *
 * Java 21+ virtual threads are lightweight threads managed by the JVM.
 * They are cheap to create (millions possible) and ideal for I/O-bound,
 * high-concurrency workloads. This demo shows the fundamental ways to
 * create and inspect a virtual thread.
 *
 * Compile & run:
 *   javac --enable-preview --release 27 VirtualThreadsIntro.java
 *   java  --enable-preview VirtualThreadsIntro
 */
public class VirtualThreadsIntro {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Thread.startVirtualThread ===");
        Thread vt1 = Thread.startVirtualThread(() -> {
            System.out.println("  hello from " + Thread.currentThread());
        });
        vt1.join();

        System.out.println("=== 2. Thread.ofVirtual().start ===");
        Thread vt2 = Thread.ofVirtual()
                .name("vt-worker-", 0)
                .start(() -> {
                    System.out.println("  " + Thread.currentThread() + " isVirtual=" + Thread.currentThread().isVirtual());
                });
        vt2.join();

        System.out.println("=== 3. Unstarted virtual thread ===");
        Thread vt3 = Thread.ofVirtual().unstarted(() -> System.out.println("  unstarted ran"));
        System.out.println("  state before start: " + vt3.getState());
        vt3.start();
        vt3.join();
        System.out.println("  state after join:  " + vt3.getState());

        System.out.println("=== 4. Inspecting properties ===");
        Thread t = Thread.currentThread();
        System.out.println("  main thread: " + t + " isVirtual=" + t.isVirtual());
        Thread v = Thread.ofVirtual().unstarted(() -> {});
        System.out.println("  virtual thread default daemon: " + v.isDaemon());
        System.out.println("  virtual thread default priority: " + v.getPriority());
        // Virtual threads are always daemon threads; priority is fixed to NORM_PRIORITY.

        System.out.println("=== 5. Virtual thread factory ===");
        java.util.concurrent.ThreadFactory factory = Thread.ofVirtual().factory();
        Thread fromFactory = factory.newThread(() -> System.out.println("  created via factory: " + Thread.currentThread()));
        fromFactory.start();
        fromFactory.join();

        System.out.println("DONE");
    }

}