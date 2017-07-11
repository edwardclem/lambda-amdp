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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class BurlapMultiDemonstrationDataset implements IDataCollection<BurlapMultiDemonstration> {

    private final List<BurlapMultiDemonstration> dataset;

    public BurlapMultiDemonstrationDataset(List<BurlapMultiDemonstration> dataset) {
        this.dataset = dataset;
    }

    public static BurlapMultiDemonstrationDataset readFromFile(File f) throws IOException {
        final String fileString = FileUtils.readFile(f); //using SPF

        List<String> splitString = Arrays.asList(fileString.split("\n\n"));

        List<BurlapMultiDemonstration> processed = splitString.stream()
                .map(s -> BurlapMultiDemonstration.parse(s))
                .collect(Collectors.toList());

        return new BurlapMultiDemonstrationDataset(processed);
    }

    @Override
    public int size() {
        return dataset.size();
    }

    @Override
    public Iterator<BurlapMultiDemonstration> iterator() {
        return dataset.iterator();
    }

    @Override
    public String toString() {
        final Iterator<BurlapMultiDemonstration> iterator = dataset.iterator();
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static class Creator implements IResourceObjectCreator<BurlapMultiDemonstrationDataset> {

        @Override
        public BurlapMultiDemonstrationDataset create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            try {
                return BurlapMultiDemonstrationDataset.readFromFile(params.getAsFile("file"));
            } catch (final IOException e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public String type(){
            return "data.bdm"; //"burlap demonstration"
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapMultiDemonstrationDataset.class)
                    .setDescription("dataset of multi instructions paired a list of true or false pre and postcondition OO-MDP states.")
                    .addParam("file", "file", "Dataset file.")
                    .addParam("genlex", "id", "lexical generator.").build(); //not sure why GENLEX is needed.
        }

    }
}
