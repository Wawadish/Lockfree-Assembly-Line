import java.util.ArrayList;
import java.util.Arrays;

// An assembly with dependencies.
public class AssemblyStation extends AbstractAssembly{
    private ArrayList<AbstractAssembly> aDependencies;

    public AssemblyStation(int p, int c, AbstractAssembly... pDependencies){
        super(p, c);
        aDependencies = new ArrayList<>(Arrays.asList(pDependencies));
    }

    // Blocks on each dependency until fetches a part, then adds a part to its linked list
    @Override
    public void generatePart() {
        // For each dependency attempt to get remove a part from the dependency's linked list until successful
        aDependencies.stream().forEach(dependency -> {while(!dependency.removePart() && AbstractAssembly.active){Thread.yield();} });
        aProducedParts.add();
    }
}
