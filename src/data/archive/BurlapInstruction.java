package data.archive;

import amdp.cleanup.state.CleanupState;
import edu.cornell.cs.nlp.spf.data.situated.sentence.SituatedSentence;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;

/**
 * Created by edwardwilliams on 6/21/17. Using Mina Rhee's type definitions.
 * A single instruction paired with a BURLAP state as a precondition
 * (i.e. state when command was issued).
 *NOTE: getting rid of this class for now.
 */
public class BurlapInstruction extends SituatedSentence<CleanupState> {

    public BurlapInstruction(Sentence sentence, CleanupState precond){
        super(sentence, precond);
    }

}
