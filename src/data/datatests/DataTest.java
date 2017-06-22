package data.datatests;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.DataHelpers;
import data.InstructionPrecond;
import data.InstructionPrecondPostcond;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
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

        CleanupState reconstructed = DataHelpers.loadStateFromString(stateString);

        //System.out.println(reconstructed);

        Assert.assertEquals("Reconstructed state equal to original state", reconstructed.toString(), initialState.toString());
        ;
        String testString = new StringBuilder().append("Don't even bother.").append("\n").append(stateString).append("\n").append(stateString).toString();

        InstructionPrecondPostcond testIPP = InstructionPrecondPostcond.parse(testString);

        System.out.println(stateString);


        Assert.assertEquals("InstructionPrecondPostcond parsing and printing correctly", testString, testIPP.toString());
    }


}
