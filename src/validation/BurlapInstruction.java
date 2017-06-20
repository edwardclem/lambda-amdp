package validation;

import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.situated.sentence.SituatedSentence;

public class BurlapInstruction extends SituatedSentence<State> {

    public BurlapInstruction(Sentence sentence, State state) {
        super(sentence, state);
    }
}