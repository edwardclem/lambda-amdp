package data.archive;

import edu.cornell.cs.nlp.spf.data.collection.IDataCollection;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.utils.io.FileUtils;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by edwardwilliams on 6/21/17.
 * Derived from the InstructionTraceDataset file in the navi repo.
 */
public class BurlapDemonstrationDataset implements IDataCollection<BurlapDemonstration> {
    private final List<BurlapDemonstration>items;

    public BurlapDemonstrationDataset(List<BurlapDemonstration> items){
        this.items = items;
    }
    //loading from file
    public static BurlapDemonstrationDataset readFromFile(File f) throws IOException{
        final String fileString = FileUtils.readFile(f); //using SPF

        List<String> splitString = Arrays.asList(fileString.split("\n\n"));

        //Java has Map now, I guess?
        //This is kind of grody, but probably easier to read than Yoav's code.
        //Less informative exceptions, though.
        List<BurlapDemonstration> processed = splitString.stream()
                .map(s -> BurlapDemonstration.parse(s))
                .collect(Collectors.toList());

        return new BurlapDemonstrationDataset(processed);
    }


    @Override
    public Iterator<BurlapDemonstration> iterator(){
        return items.iterator();
    }


    public int size(){
        return items.size();
    }

    @Override
    public String toString() {
        final Iterator<BurlapDemonstration> iterator = items.iterator();
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    public static class Creator implements IResourceObjectCreator<BurlapDemonstrationDataset>{

        @Override
        public BurlapDemonstrationDataset create(Parameters params, IResourceRepository repo){
            try {
                return BurlapDemonstrationDataset.readFromFile(params.getAsFile("file"));
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
            return new ResourceUsage.Builder(type(), BurlapDemonstrationDataset.class)
                    .setDescription("dataset of instructions paired with precondition and postcondition OO-MDP states.")
                    .addParam("file", "file", "Dataset file.")
                    .addParam("genlex", "id", "lexical generator.").build(); //not sure why GENLEX is needed.
        }

    }
}
