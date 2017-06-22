package data;

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
public class InstructionPrecondPostcondDataset implements IDataCollection<InstructionPrecondPostcond> {
    private final List<InstructionPrecondPostcond>items;

    public InstructionPrecondPostcondDataset(List<InstructionPrecondPostcond> items){
        this.items = items;
    }
    //loading from file
    public static InstructionPrecondPostcondDataset readFromFile(File f) throws IOException{
        final String fileString = FileUtils.readFile(f); //using SPF

        List<String> splitString = Arrays.asList(fileString.split("\n\n"));

        //Java has Map now, I guess?
        //This is kind of grody, but probably easier to read than Yoav's code.
        //Less informative exceptions, though.
        List<InstructionPrecondPostcond> processed = splitString.stream()
                .map(s -> InstructionPrecondPostcond.parse(s))
                .collect(Collectors.toList());

        return new InstructionPrecondPostcondDataset(processed);
    }


    @Override
    public Iterator<InstructionPrecondPostcond> iterator(){
        return items.iterator();
    }


    public int size(){
        return items.size();
    }

    @Override
    public String toString() {
        final Iterator<InstructionPrecondPostcond> iterator = items.iterator();
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    public static class Creator implements IResourceObjectCreator<InstructionPrecondPostcondDataset>{

        @Override
        public InstructionPrecondPostcondDataset create(Parameters params, IResourceRepository repo){
            try {
                return InstructionPrecondPostcondDataset.readFromFile(params.getAsFile("file"));
            } catch (final IOException e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public String type(){
            return "data.ipp"; //"instruction precond postcond"
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), InstructionPrecondPostcondDataset.class)
                    .setDescription("dataset of instructions paired with precondition and postcondition OO-MDP states.")
                    .addParam("file", "file", "Dataset file.")
                    .addParam("genlex", "id", "lexical generator.").build(); //not sure why GENLEX is needed.
        }

    }
}
