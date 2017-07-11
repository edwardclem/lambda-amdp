package validation;


import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.state.State;
import data.BurlapMultiDemonstration;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.utils.composites.Pair;
import edu.cornell.cs.nlp.utils.composites.Triplet;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import lambdas.LambdaPlanning.LambdaConverter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class BurlapMultiValidator<DI extends ILabeledDataItem<Sentence,List<Triplet<State,State,Boolean>>>, MR extends LogicalExpression> implements IBurlapValidator<BurlapMultiDemonstration, MR>{

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
    public boolean isValid(BurlapMultiDemonstration var2, MR var1) {

        String parse_string = var1.toString();

        if (!parse_string.contains("#")){
            String pred = LambdaConverter.convert(var1.toString());
            List<Triplet<State,State,Boolean>> dataset = var2.getLabel();

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
}

