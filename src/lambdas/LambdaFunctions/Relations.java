package lambdas.LambdaFunctions;

import amdp.cleanup.CleanupDomain;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import edu.cornell.cs.nlp.utils.composites.Pair;

/**
 * Containing functions that define relationships between two objects.
 */
public class Relations {

    static double NEAR_THRESHOLD = 1.5; //threshold for "near" cutoff, also used for "left" and "right"

    public static double dist(String obj1, String obj2, OOState state){
        Pair<Double, Double> obj1Coords = Attributes.location(obj1, state);
        Pair<Double, Double> obj2Coords = Attributes.location(obj2, state);
        return Math.sqrt(Math.pow(obj1Coords.first() - obj2Coords.first(), 2) + Math.pow(obj1Coords.second() - obj2Coords.second(), 2));
    }

    public static Boolean near(String obj1, String obj2, OOState state) {

        //Check if any objects are null, return false if so
        //also check if any of the attributes are a region
        return !obj1.equals("") && !obj2.equals("")  && !Attributes.isRegion(obj1, state) && !Attributes.isRegion(obj2, state) && dist(obj1, obj2, state) <= NEAR_THRESHOLD &&  dist(obj1, obj2, state) > 0;

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
     * @param obj1
     * @param obj2
     * @param state
     * @return
     */
    public static Boolean left(String obj1, String obj2, OOState state){
        if (!obj1.equals("") && !obj2.equals("") && !Attributes.isRegion(obj1, state) && !Attributes.isRegion(obj2, state)){
            Pair<Double, Double> loc1 = Attributes.location(obj1, state);
            Pair<Double, Double> loc2 = Attributes.location(obj2, state);
            return loc1.first() < loc2.first() && dist(obj1, obj2, state) <= NEAR_THRESHOLD &&  dist(obj1, obj2, state) > 0 && (loc1.second().equals(loc2.second()));
        } else{
            return false;
        }
    }

    /**
     * Returns true of obj1 is to the right of obj2
     * @param obj1
     * @param obj2
     * @param state
     * @return
     */
    public static Boolean right(String obj1, String obj2, OOState state){
        if (!obj1.equals("") && !obj2.equals("") && !Attributes.isRegion(obj1, state) && !Attributes.isRegion(obj2, state)){
            Pair<Double, Double> loc1 = Attributes.location(obj1, state);
            Pair<Double, Double> loc2 = Attributes.location(obj2, state);
            return loc1.first() > loc2.first() && dist(obj1, obj2, state) <= NEAR_THRESHOLD && dist(obj1, obj2, state) > 0 && (loc1.second().equals(loc2.second()));
        } else{
            return false;
        }
    }


    /**
     * Used as part of argmax-based adjectives.
     * State not used, as object ID should be sufficient.
     * @param obj1
     * @param obj2
     * @param state
     * @return
     */
    public static boolean eq(String obj1, String obj2, OOState state){
        return obj1.equals(obj2);
    }



}
