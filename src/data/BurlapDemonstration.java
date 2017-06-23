package data;

import amdp.cleanup.state.CleanupState;
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
public class BurlapDemonstration implements ILabeledDataItem<Sentence, Pair<CleanupState, CleanupState>> {

    private final Sentence instruction;
    private final Pair<CleanupState, CleanupState> conditions;


    public BurlapDemonstration(Sentence sentence, CleanupState precond, CleanupState postcond){
        this.instruction = new Sentence(sentence);
        this.conditions =  Pair.of(precond, postcond);
    }

    //TODO: does this need to know the domain? I don't think so?
    public static BurlapDemonstration parse(String string){
        final String[] split = string.split("\n");
        final Sentence sentence = new Sentence(split[0]);
        final CleanupState precondition = DataHelpers.loadStateFromString(split[1]);
        final CleanupState postcondition = DataHelpers.loadStateFromString(split[2]);
        return new BurlapDemonstration(sentence, precondition, postcondition);
    }

    @Override
    public Sentence getSample() {
        return instruction;
    }

    @Override
    public Pair<CleanupState, CleanupState> getLabel() {
        return conditions;
    }

    @Override
    //TODO: figure out what this is for?
    public boolean isCorrect(Pair<CleanupState, CleanupState> conditions) {
        return true;
    }

    @Override
    public String toString(){
        return new StringBuilder().append(instruction.getString()).append("\n")
                .append(DataHelpers.ooStateToStringCompact(conditions.first())).append("\n")
                .append(DataHelpers.ooStateToStringCompact(conditions.second())).toString();
    }


}
