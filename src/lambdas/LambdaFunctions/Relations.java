package lambdas.LambdaFunctions;

import amdp.cleanup.CleanupDomain;
import burlap.mdp.core.oo.state.OOState;
import edu.cornell.cs.nlp.utils.composites.Pair;

/**
 * Containing functions that define relationships between two objects.
 */
public class Relations {

    static double NEAR_THRESHOLD = 1.0; //threshold for "near" cutoff

    public static Boolean near(String obj1, String obj2, OOState state){

        //Check if any objects are null, return false if so
        if (!obj1.equals("") && !obj2.equals("")){
            return dist(obj1, obj2, state) <= NEAR_THRESHOLD;
        } else{
            return false;
        }

    }

    public static Boolean in(String obj1, String obj2, OOState state){

        //Check conditions for valid check
        if (!obj1.equals("") && !obj2.equals("") && Attributes.isRegion(obj2, state) && !Attributes.isRegion(obj1, state)){
            //get location of first point
            Pair<Double, Double> obj1Location = Attributes.location(obj1, state);
            return CleanupDomain.regionContainsPoint(state.object(obj2), obj1Location.first().intValue(), obj1Location.second().intValue(), false);
        } else{
            return false;
        }
    }

    /**
     * returns true if obj1 is left of obj2
     * TODO: is this ordering a problem?
     * @param obj1
     * @param obj2
     * @param state
     * @return
     */
//    public static Boolean left(String obj1, String obj2, OOState state){
//
//    }
//
//    public static Boolean right(String obj1, String obj2, OOState state){
//
//    }

    public static double dist(String obj1, String obj2, OOState state){
        Pair<Double, Double> obj1Coords = Attributes.location(obj1, state);
        Pair<Double, Double> obj2Coords = Attributes.location(obj2, state);

        return Math.sqrt(Math.pow(obj1Coords.first() - obj2Coords.first(), 2) + Math.pow(obj1Coords.second() - obj2Coords.second(), 2));

    }


}
