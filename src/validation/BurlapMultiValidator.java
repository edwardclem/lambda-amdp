package validation;


import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.state.State;
import data.BurlapMultiDemonstration;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.utils.composites.Pair;
import edu.cornell.cs.nlp.utils.composites.Triplet;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import lambdas.LambdaPlanning.LambdaConverter;
import supervised.BurlapResourceRepo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class BurlapMultiValidator<DI extends ILabeledDataItem<Sentence,List<Triplet<State,State,Boolean>>>, MR extends LogicalExpression> implements IValidator<DI, MR> {

    private static JScheme js;
    private String preds = "src/CleanupPredicates.scm";

    public BurlapMultiValidator() {
        js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }

    //@Override
    public boolean isValid(DI dataItem, MR var1) {

        String parse_string = var1.toString();

        if (!parse_string.contains("#")){
            String pred = LambdaConverter.convert(var1.toString());
            List<Triplet<State,State,Boolean>> dataset = dataItem.getLabel();

            for(Triplet<State,State,Boolean> triple : dataset) {
                CleanupState initialState = (CleanupState) triple.first();
                js.setGlobalValue("initialState", initialState);
                js.eval("(define the (satisfiesPredicate initialState))");
                CleanupState after = (CleanupState) triple.second();
                SchemeProcedure pf = (SchemeProcedure)js.eval(pred);
                Boolean istrue = (boolean) js.call(pf,after);
                if(triple.third()) {
                    if(!istrue || (boolean) js.call(pf,initialState)) {
                        return false;
                    }
                }
                else {
                    if(istrue) {
                        return false;
                    }
                }
            }
            return true;
        }
        else{
            return false;
        }

    }

    public static class Creator implements IResourceObjectCreator<BurlapMultiValidator> {

        @Override
        public BurlapMultiValidator create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            return new BurlapMultiValidator();
        }

        @Override
        public String type(){
            return "validator.burlap.multi";
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapMultiValidator.class)
                    .setDescription("validator that checks if the logical expression correctly evaluates on provided set of pre - and post- condition states given in the demonstration.")
                    .build();
        }
    }
}

