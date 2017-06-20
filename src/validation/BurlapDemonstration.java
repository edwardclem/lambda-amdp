package validation;

import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;

public class BurlapDemonstration implements IDataItem<BurlapInstruction> {

    BurlapInstruction instruction;
    State afterState;

    public BurlapDemonstration(String command, State beforeState, State afterState) {
        this.instruction = new BurlapInstruction(new Sentence(command),beforeState);
        this.afterState = afterState;
    }

    @Override
    public BurlapInstruction getSample() {
        return instruction;
    }


    public State getAfterState() {
        return afterState;
    }

}
