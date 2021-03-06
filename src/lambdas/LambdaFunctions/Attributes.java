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
     * Checks if the given object is of the correct class.
     * Maybe overkill to move this to Java, but going for consistency.
     * @param classType
     * @param obj
     * @param state
     * @return
     */
    public static Boolean checkClass(String classType, String obj, OOState state){
        return classType.equals(state.object(obj).className());
    }

    /**
     *
     * @param attribute
     * @param targetValue
     * @param obj
     * @param state
     * @return
     */
    public static Boolean checkAttribute(String attribute, String targetValue, String obj, OOState state) {
        //get object instance
        ObjectInstance objectInstance = state.object(obj);

        //return false if the object does not have the attribute
        return objectInstance.variableKeys().contains(attribute) && targetValue.equals(objectInstance.get(attribute));
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
            //casting to double
            Double right = ((Integer)objInstance.get("right")).doubleValue();
            Double left = ((Integer)objInstance.get("left")).doubleValue();
            Double top = ((Integer)objInstance.get("top")).doubleValue();
            Double bottom = ((Integer)objInstance.get("bottom")).doubleValue();

            //mean of coords
            Double xCenter = (right + left)/2.0;
            Double yCenter = (top + bottom)/2.0;

            return Pair.of(xCenter, yCenter);

        } else{
            return Pair.of(((Integer)objInstance.get("x")).doubleValue(), ((Integer)objInstance.get("y")).doubleValue());
        }
    }

    /**
     * Returns the size of an object. If a region, use the dimensions of the region,
     * if not, check the color attribute (blech)
     * TODO: fix this when we fix our state description
     * TODO: these should always be integers I think?
     * @param obj
     * @param state
     * @return
     */
    public static double size(String obj, OOState state){

        ObjectInstance objInstance = state.object(obj);

        if (isRegion(obj, state)){
            Integer right = ((Integer)objInstance.get("right"));
            Integer left = ((Integer)objInstance.get("left"));
            Integer top = ((Integer)objInstance.get("top"));
            Integer bottom = ((Integer)objInstance.get("bottom"));

            Integer size = (right - left)*(top - bottom);

            return size.doubleValue();
        } else {
            //check if object is an agent

            if (checkClass("agent", obj, state)){
                return 2.0; //default size
            } else {
                String color = (String)objInstance.get("colour");

                switch (color){
                    case "large": return 3.0;
                    case "small": return 1.0;
                    default: return 2.0;
                }
            }


        }
    }
}
