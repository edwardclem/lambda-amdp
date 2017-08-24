package lambdas.LambdaFunctions;

import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import jscheme.JScheme;
import jscheme.SchemeProcedure;

import java.util.Collections;
import java.util.List;

public class Determiners{

    /**
     * Returns the object ID of the first object that satisfies the predicate.
     * Used to implement definite determiner.
     * TODO: handle determiner ambiguity
     * @param state
     * @param predicate
     * @return
     */
    public static String satisfiesPredicate(OOState state, SchemeProcedure predicate){

        JScheme js = new JScheme();

        List<ObjectInstance> objects = state.objects();
        Collections.shuffle(objects);

        for (ObjectInstance o: objects){

            //returns a procedure that can be tested on a state
            SchemeProcedure predicateFromName = (SchemeProcedure)js.call(predicate, o.name());

            if((Boolean)js.call(predicateFromName,state)){
                return o.name();
            }
        }


        return "";

    }
}
