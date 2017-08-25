package lambdas.LambdaFunctions;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import edu.cornell.cs.nlp.utils.composites.Pair;

/**
 * Attributes of single objects
 */
public class Attributes {

    /**
     * Returns true if the object is a region, i.e. a room or door in CleanupDomain.
     * @param obj
     * @param state
     * @return
     */
    public static Boolean isRegion(String obj, OOState state){
        return state.object(obj).className().equals("room") || state.object(obj).className().equals("door");

    }

    /**
     * Return pair corresponding to location of the object. If the object is a region, get the center point.
     * @param obj
     * @param state
     * @return
     */
    public static Pair<Double, Double> location(String obj, OOState state){

        ObjectInstance objInstance = state.object(obj);

        if (isRegion(obj, state)){
            //getting coordinates of region bounds
            Double right = (Double)objInstance.get("right");
            Double left = (Double)objInstance.get("left");
            Double top = (Double)objInstance.get("top");
            Double bottom = (Double)objInstance.get("bottom");

            //mean of coords
            Double xCenter = (right + left)/2.0;
            Double yCenter = (top + bottom)/2.0;

            return Pair.of(xCenter, yCenter);

        } else{
            return Pair.of((Double)objInstance.get("x"), (Double)objInstance.get("y"));
        }
    }
}
