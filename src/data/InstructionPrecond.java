package data;

import amdp.cleanup.state.CleanupState;
import edu.cornell.cs.nlp.spf.data.situated.sentence.SituatedSentence;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;

/**
 * Created by edwardwilliams on 6/21/17.
 * A single instruction paired with a BURLAP state as a precondition
 * (i.e. state when command was issued).
 *
 */
public class InstructionPrecond extends SituatedSentence<CleanupState> {

    public InstructionPrecond(Sentence sentence, CleanupState precond){
        super(sentence, precond);
    }

}
