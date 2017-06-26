package validation;

import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import data.BurlapDemonstration;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import jscheme.JScheme;
import jscheme.SchemeProcedure;

import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import lambdas.LambdaPlanning.LambdaConverter;

/**
 * Validates logical expression by testing whether or not it accepts the provided termination state as a termination state.
 * @author Mina Rhee and Edward Williams
 */

public class BurlapValidator implements IValidator<BurlapDemonstration, LogicalExpression> {

    //TODO: initialize this "executor" as a resource instead
    private static JScheme js;
    private String preds = "src/CleanupPredicates.scm";

    public BurlapValidator() {
        js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }

    @Override
    public boolean isValid(BurlapDemonstration dataItem, LogicalExpression parse) {
        String parse_string = parse.toString();

        //preprocessing - removing abstract lexical templates
        //TODO: this is a crude pruning - see if a bug is happening in GENLEX.
        if (!parse_string.contains("#")){
            String pred = LambdaConverter.convert(parse.toString());
            CleanupState initialState = dataItem.getLabel().first();
            js.setGlobalValue("initialState", (CleanupState) initialState);
            js.eval("(define the (satisfiesPredicate initialState))");
            CleanupState after = dataItem.getLabel().second();
            SchemeProcedure pf = (SchemeProcedure)js.eval(pred);
            return (Boolean) js.call(pf,after);
        }
        else{
            return false;
        }



    }

    public static class Creator implements IResourceObjectCreator<BurlapValidator>{

        @Override
        public BurlapValidator create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            return new BurlapValidator();
        }

        @Override
        public String type(){
            return "validator.burlap";
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapValidator.class)
                    .setDescription("validator that checks if the logical expression correctly evaluates on the pre - and post- condition states given in the demonstration.")
                    .build();
        }
    }

}