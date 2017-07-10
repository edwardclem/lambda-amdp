package validation;

import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.utils.composites.Pair;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import java.io.FileNotFoundException;
import java.io.FileReader;
import lambdas.LambdaPlanning.LambdaConverter;

/**
 * Validates logical expression by testing whether or not it accepts the provided termination state as a termination state.
 * @author Mina Rhee and Edward Williams
 */
public class BurlapSingleValidator<DI extends ILabeledDataItem<?, Pair<State, State>>, MR extends LogicalExpression> implements IValidator<DI, MR> {

    //TODO: initialize this as a resource + specify predicates in experiment file
    private static JScheme js;
    private String preds = "src/CleanupPredicates.scm";

    public BurlapSingleValidator() {
        js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }

    @Override
    public boolean isValid(DI dataItem, MR parse) {
        String parse_string = parse.toString();

        //preprocessing - removing abstract lexical templates
        //TODO: this is a crude pruning - see if a bug is happening in GENLEX.
        if (!parse_string.contains("#")){
            String pred = LambdaConverter.convert(parse.toString());
            CleanupState before = (CleanupState) dataItem.getLabel().first();
            js.setGlobalValue("initialState", before);
            //set determiner with respect to initial state
            js.eval("(define the (satisfiesPredicate initialState))");
            CleanupState after = (CleanupState) dataItem.getLabel().second();
            SchemeProcedure pf = (SchemeProcedure)js.eval(pred);
            //should also be false during the before state!!!!!
            return ((Boolean) js.call(pf,after)) && !((Boolean) js.call(pf,before));
        }
        else{
            return false;
        }



    }

    public static class Creator implements IResourceObjectCreator<BurlapSingleValidator>{

        @Override
        public BurlapSingleValidator create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            return new BurlapSingleValidator();
        }

        @Override
        public String type(){
            return "validator.burlap.single";
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapSingleValidator.class)
                    .setDescription("validator that checks if the logical expression correctly evaluates on the pre - and post- condition states given in the demonstration.")
                    .build();
        }
    }

}