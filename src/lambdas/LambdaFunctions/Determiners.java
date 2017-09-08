package lambdas.LambdaFunctions;

import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import jscheme.JScheme;
import jscheme.SchemeProcedure;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * strict definite determiner - returns an object if only ONE object satisfies the predicate.
     * Otherwise, returns null.
     * @param state
     * @param predicate
     * @return
     */
    public static String definiteDeterminerOld(OOState state, SchemeProcedure predicate){

        String grounding = "";
        int numCorrect = 0; //track number of examples that satisfy the predicate


        JScheme js = new JScheme();

        List<ObjectInstance> objects = state.objects();
        Collections.shuffle(objects);

        for (ObjectInstance o: objects){

            //returns a procedure that can be tested on a state
            SchemeProcedure predicateFromName = (SchemeProcedure)js.call(predicate, o.name());

            if((Boolean)js.call(predicateFromName,state)){
                grounding = o.name();
                numCorrect++;
            }
        }

        if (numCorrect == 1){
            return grounding;
        } else{
            return "";
        }
    }
    /**
     * strict definite determiner - returns an object if only ONE object satisfies the predicate.
     * Otherwise, returns null.
     * @param state
     * @param predicate
     * @return
     */
    public static String definiteDeterminer(OOState state, SchemeProcedure predicate){

        List<ObjectInstance> objectsSatisfying = getObjectsSatsifyingPredicate(state, predicate);

        if (objectsSatisfying.size() == 1){
            return objectsSatisfying.get(0).name();
        } else{
            return "";
        }
    }

    /**
     * returns a list of all objects satisfying the predicate.
     * @param state
     * @param predicate
     * @return
     */
    public static List<ObjectInstance> getObjectsSatsifyingPredicate(OOState state, SchemeProcedure predicate){

        JScheme js = new JScheme();

        //get list fo all objects satisfying predicate
        return state.objects().stream()
                        .filter(o -> (Boolean)js.call((SchemeProcedure)js.call(predicate, o.name()),state))
                        .collect(Collectors.toList());


    }

    /**
     * Returns the argmax over the set of objects satisfying the predicate.
     * measure takes an object and returns a double.
     * Returns blank string if none satisfy the predicate.
     * @param state
     * @param measure
     * @param predicate
     * @return
     */
    public static String argmax(OOState state, SchemeProcedure measure, SchemeProcedure predicate){

        String currentMax = "";
        double currentMaxValue = Double.MIN_VALUE;
        //TODO: shared evaluator somehow
        JScheme js = new JScheme();

        List<ObjectInstance> objectsSatisfying = getObjectsSatsifyingPredicate(state, predicate);

        if (objectsSatisfying.size() == 1){
            return ""; // don't use argmax if there's only one
        }

        for (ObjectInstance o: objectsSatisfying){
            if (currentMax.equals("")){
                currentMax = o.name();
                SchemeProcedure measureFromName = (SchemeProcedure)js.call(measure, o.name());
                currentMaxValue = (Double)js.call(measureFromName, state);
            } else {
                //check value of new and old
                SchemeProcedure measureFromNameNew = (SchemeProcedure) js.call(measure, o.name());
                double currentVal = (Double) js.call(measureFromNameNew, state);
                if (currentVal > currentMaxValue) {
                    currentMax = o.name();
                    currentMaxValue = currentVal;
                }
            }
        }
        return currentMax;
    }

    public static String argmin(OOState state, SchemeProcedure measure, SchemeProcedure predicate){

        String currentMin = "";
        double currentMinValue = Double.MAX_VALUE ; //assuming "measure" is always positive? shouldn't be a problem in practice tho
        //TODO: shared evaluator somehow
        JScheme js = new JScheme();

        List<ObjectInstance> objectsSatisfying = getObjectsSatsifyingPredicate(state, predicate);

        if (objectsSatisfying.size() == 1){
            return ""; // don't use argmax if there's only one
        }

        for (ObjectInstance o: objectsSatisfying){
            if (currentMin.equals("")){
                currentMin = o.name();
                SchemeProcedure measureFromName = (SchemeProcedure)js.call(measure, o.name());
                currentMinValue = (Double)js.call(measureFromName, state);
            } else {
                //check value of new and old
                SchemeProcedure measureFromNameNew = (SchemeProcedure)js.call(measure, o.name());
                double currentVal = (Double)js.call(measureFromNameNew, state);
                if (currentVal < currentMinValue){
                    currentMin = o.name();
                    currentMinValue = currentVal;
                }
            }
        }
        return currentMin; //will be null if nothing satisfies the predicate
    }
}
