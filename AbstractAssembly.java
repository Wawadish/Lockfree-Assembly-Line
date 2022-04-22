import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractAssembly implements Runnable {

    protected static volatile boolean active = true; // stops all instances when set to false

    private int aSleepTime;
    protected int aMaxCapacity;
    protected volatile MyLinkedList aProducedParts;

    public AbstractAssembly(int p, int c) {
        Random r = new Random();
        aSleepTime = ThreadLocalRandom.current().nextInt(p + 1);//(int) Math.random() * (p + 1); //r.nextInt(p);
        //System.out.println(String.format("%s sleep time %d", this.getClass().getName(), aSleepTime));
        aMaxCapacity = c;

        // Sets the linked list implementation depending on the launcher Q1a, Q1b
        if (Launcher.isBlocking) {
            aProducedParts = new BlockingLinkedList();
        } else {
            aProducedParts = new LockFreeLinkedList();
        }
    }

    public boolean removePart(){
        return aProducedParts.removeFirst();
    }

    // Part generation must be defined by the subclasses
    public abstract void generatePart();

    // Overwritten by RootStation to set AbstractAssembly.active = false;
    public void maxQueueHandler(){
        aProducedParts.clean();
    }

    @Override
    public void run() {
         // I checked that this was thread safe
        while (active) {
            // Sleep p ms
            try {Thread.sleep(aSleepTime);} catch (InterruptedException e) {}

            // Remove marked nodes, if instance is AssemblyRoot set AbstractAssembly.active = false
            if(aProducedParts.size() >= aMaxCapacity){
                maxQueueHandler();
                // If still full go to sleep
                if(aProducedParts.size() >= aMaxCapacity){
                    continue;
                }
            }

            // Upon completion, regardless of the context, a new node will be added to the instance's list.
            generatePart();
        }
    }

}
