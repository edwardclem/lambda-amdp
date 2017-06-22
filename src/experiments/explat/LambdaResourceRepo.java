package experiments.explat;

import data.InstructionPrecondPostcondDataset;
import edu.cornell.cs.nlp.spf.explat.resources.ResourceCreatorRepository;

/**
 * Created by edwardwilliams on 6/22/17.
 * Register resources with the resource creator.
 */
public class LambdaResourceRepo extends ResourceCreatorRepository {

    public LambdaResourceRepo(){
        //register data collection
        registerResourceCreator(new InstructionPrecondPostcondDataset.Creator());

        //TODO: all of these things.

        //register parsing resources

        //register lexicon resources

        //register genlex resources

        //register learning algorithm resources


    }
}
