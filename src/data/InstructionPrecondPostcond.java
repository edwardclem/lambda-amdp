package data;

import amdp.cleanup.state.CleanupState;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;

/**
 * Created by edwardwilliams on 6/21/17.
 */
public class InstructionPrecondPostcond implements ILabeledDataItem<InstructionPrecond, CleanupState> {

    private final InstructionPrecond instruction;
    private final CleanupState postcond;


    public InstructionPrecondPostcond(Sentence sentence, CleanupState precond, CleanupState postcond){
        this.instruction = new InstructionPrecond(sentence, precond);
        this.postcond = postcond;
    }

    //TODO: does this need to know the domain? I don't think so?
    public static InstructionPrecondPostcond parse(String string){
        final String[] split = string.split("\n");
        final Sentence sentence = new Sentence(split[0]);
        final CleanupState precondition = DataHelpers.loadStateFromString(split[1]);
        final CleanupState postcondition = DataHelpers.loadStateFromString(split[2]);
        return new InstructionPrecondPostcond(sentence, precondition, postcondition);
    }

    @Override
    public InstructionPrecond getSample() {
        return instruction;
    }

    @Override
    public CleanupState getLabel() {
        return postcond;
    }

    @Override
    //TODO: figure out what this is for?
    public boolean isCorrect(CleanupState state) {
        return true;
    }

    @Override
    public String toString(){
        return new StringBuilder().append(instruction.getString()).append("\n")
                .append(DataHelpers.ooStateToStringCompact(instruction.getState())).append("\n")
                .append(DataHelpers.ooStateToStringCompact(postcond)).toString();
    }


}
