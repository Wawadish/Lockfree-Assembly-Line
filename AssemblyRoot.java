public class AssemblyRoot extends AssemblyStation{

    private int aProducedCount = 0;

    public AssemblyRoot(int p, int k, AbstractAssembly... pDependencies) {
        super(p, k, pDependencies);
    }

    // Prints the parts every 100 parts as a progress update
    @Override
    public void generatePart(){
        super.generatePart();
        aProducedCount++;
        if(aProducedCount % 100 == 0){
            System.out.println(String.format("Produce new part, total: %d/%d", aProducedCount, aMaxCapacity));
        }
    }

    // Stops the system when the linked list is full
    @Override
    public void maxQueueHandler() {
        AbstractAssembly.active = false;
    }
}
