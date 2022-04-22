import java.util.ArrayList;

public class Launcher {

    public static boolean isBlocking;

    public static void start(String[] args, boolean pBlocking){

        // Varies depending on Q1a, Q1,b
        isBlocking = pBlocking;

        // Checks args
        if(args.length != 3){
            System.out.println(String.format("Expected 3 arguments but got %d.", args.length));
            System.exit(1);
        }

        // Parse args
        int p = Integer.parseInt(args[0]);
        int c = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);
        System.out.println(String.format("p: %d\tc: %d\tk: %d", p, c, k));

        // Build the tree as shown in the assignment
        AssemblyReservoir reservoirs[] = new AssemblyReservoir[] {
                new AssemblyReservoir(p, c),new AssemblyReservoir(p, c), new AssemblyReservoir(p, c)
        };

        AssemblyStation intermediates[] = new AssemblyStation[5];
        intermediates[0] = new AssemblyStation(p, c, reservoirs[0]);
        intermediates[1] = new AssemblyStation(p, c, reservoirs[1], reservoirs[2]);
        intermediates[2] = new AssemblyStation(p, c, reservoirs[1], intermediates[0]);
        intermediates[3] = new AssemblyStation(p, c, intermediates[0], intermediates[1]);
        intermediates[4] = new AssemblyStation(p, c, reservoirs[2], intermediates[1]);

        AssemblyRoot root = new AssemblyRoot(p, k,
                intermediates[0], intermediates[2], intermediates[3], intermediates[4]);

        // Create threads
        ArrayList<Thread> threads = new ArrayList<>();
        for(AbstractAssembly assembly : reservoirs){
            threads.add(new Thread(assembly));
        }
        for(AbstractAssembly assembly : intermediates) {
            threads.add(new Thread(assembly));
        }
        threads.add(new Thread(root));

        // Start timer
        long start = System.currentTimeMillis();

        // Launch threads
        threads.forEach(t -> t.start());

        // Wait for the AssemblyRoot to fill up its linked list and set AbstractAssembly.active = false
        while(AbstractAssembly.active){try {Thread.sleep(1);} catch (InterruptedException e) {}}

        // Stop timer
        long end = System.currentTimeMillis();

        // Print timer results
        int millis = (int) (end - start);
        System.out.println(String.format("LinkedList type: (%s)", root.aProducedParts.getClass().getName()));
        System.out.println(String.format("Execution Time: %d.%03d seconds", millis/1000, millis%1000));

        // Join threads
        threads.forEach(t -> {try {t.interrupt(); t.join();} catch (InterruptedException e) {}});
    }
}
