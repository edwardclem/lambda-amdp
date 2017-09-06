package data;

import edu.cornell.cs.nlp.spf.data.collection.IDataCollection;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class BurlapTerminalStatePlanDataset implements IDataCollection<BurlapTerminalStatePlanDemonstration> {

    private final List<BurlapTerminalStatePlanDemonstration> dataset;

    public BurlapTerminalStatePlanDataset(List<BurlapTerminalStatePlanDemonstration> dataset) {
        this.dataset = dataset;
    }

    public static BurlapTerminalStatePlanDataset readFromFile(File f) throws IOException {
        final String fileString = FileUtils.readFile(f); //using SPF

        List<String> splitString = Arrays.asList(fileString.split("\n\n"));


        List<BurlapTerminalStatePlanDemonstration> processed = splitString.stream()
                .map(s -> BurlapTerminalStatePlanDemonstration.parse(s))
                .collect(Collectors.toList());

        Collections.shuffle(processed);

        return new BurlapTerminalStatePlanDataset(processed);
    }

    @Override
    public int size() {
        return dataset.size();
    }

    @Override
    public Iterator<BurlapTerminalStatePlanDemonstration> iterator() {
        return dataset.iterator();
    }

    @Override
    public String toString() {
        final Iterator<BurlapTerminalStatePlanDemonstration> iterator = dataset.iterator();
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static class Creator implements IResourceObjectCreator<BurlapTerminalStatePlanDataset> {

        @Override
        public BurlapTerminalStatePlanDataset create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            try {
                return BurlapTerminalStatePlanDataset.readFromFile(params.getAsFile("file"));
            } catch (final IOException e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public String type(){
            return "data.plan.state.bdm"; //"burlap demonstration"
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapTerminalStatePlanDataset.class)
                    .setDescription("dataset of multi instructions paired a list of episodes following the command.")
                    .addParam("file", "file", "Dataset file.")
                    .addParam("genlex", "id", "lexical generator.").build(); //not sure why GENLEX is needed.
        }

    }

}
