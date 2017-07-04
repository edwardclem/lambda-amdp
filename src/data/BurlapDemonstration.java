package data;

import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.utils.composites.Pair;

import static edu.cornell.cs.nlp.utils.composites.Pair.of;

/**
 * Created by edwardwilliams on 6/21/17.
 * Representing the instruction directly as a sentence. The only time the pre- or post- condition states are used
 * is during validation, and we're using traditional coarse GENLEX.
 * If we start conditioning on the initial state, then the types for everything (including validator, GENLEX, learner, model) will need to change.
 */
public class BurlapDemonstration implements ILabeledDataItem<Sentence, Pair<State, State>> {

    private final Sentence instruction;
    private final Pair<State, State> conditions;


    public BurlapDemonstration(Sentence sentence, State precond, State postcond){
        this.instruction = new Sentence(sentence);
        this.conditions =  Pair.of(precond, postcond);
    }

    public static BurlapDemonstration parse(String string){
        final String[] split = string.split("\n");
        final Sentence sentence = new Sentence(split[0]);
        final State precondition = DataHelpers.loadStateFromString(split[1]);
        final State postcondition = DataHelpers.loadStateFromString(split[2]);
        return new BurlapDemonstration(sentence, precondition, postcondition);
    }

    @Override
    public Sentence getSample() {
        return instruction;
    }

    @Override
    public Pair<State, State> getLabel() {
        return conditions;
    }

    @Override
    //NOTE: won't work in the way it's intended
    // it's supposed to be comparing the parsing output (or some byproduct like the trace) that's the same type as the label
    //this is never used with the validation-driven testing procedure
    public boolean isCorrect(Pair<State, State> conditions) {
        return true;
    }

    @Override
    public String toString(){
        return new StringBuilder().append(instruction.getString()).append("\n")
                .append(DataHelpers.ooStateToStringCompact((OOState)conditions.first())).append("\n")
                .append(DataHelpers.ooStateToStringCompact((OOState)conditions.second())).toString();
    }


}
