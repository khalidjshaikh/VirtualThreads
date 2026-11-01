/**
 * Cancellation of virtual threads via interruption.
 */
public class VirtualThreadCancellation {
    public static void main(String[] args) throws Exception {
        Thread v = Thread.ofVirtual().start(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10);
                }
                System.out.println("  noticed interrupt, exiting");
            } catch (InterruptedException e) {
                System.out.println("  interrupted while sleeping");
                Thread.currentThread().interrupt();
            }
        });
        Thread.sleep(50);
        v.interrupt();
        v.join();
        System.out.println("  thread state: " + v.getState());
        System.out.println("DONE");
    }
}