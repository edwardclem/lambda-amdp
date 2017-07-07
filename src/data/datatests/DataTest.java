package data.datatests;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.CleanupState;

import data.BurlapDemonstration;
import data.DataHelpers;
import org.junit.Assert;

/**
 * Created by edwardwilliams on 6/21/17.
 *
 * Testing data representation classes, in particular loading cleanupStates from file.
 */
public class DataTest {

    public static void main(String[] args){
        //creating cleanup state, rendering to string, then loading from string.
        //should probably put the loading function in the CleanupState class def.

        CleanupState initialState = (CleanupState)CleanupDomain.getClassicState(true);

        //System.out.println(state);
        String stateString = DataHelpers.ooStateToStringCompact(initialState);

        CleanupState reconstructed = DataHelpers.loadStateFromStringCompact(stateString);

        //System.out.println(reconstructed);

        Assert.assertEquals("Reconstructed state equal to original state", reconstructed.toString(), initialState.toString());
        ;
        String testString = new StringBuilder().append("Don't even bother.").append("\n").append(stateString).append("\n").append(stateString).toString();

        BurlapDemonstration testIPP = BurlapDemonstration.parse(testString);

        System.out.println(stateString);


        Assert.assertEquals("BurlapDemonstration parsing and printing correctly", testString, testIPP.toString());
    }


}
