package data;

import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.utils.composites.Triplet;

import java.util.List;

public class BurlapMultiDemonstration implements ILabeledDataItem<Sentence, List<Triplet<State,State,Boolean>>> {

    Sentence instruction;
    List<Triplet<State,State,Boolean>> dataset;

    public BurlapMultiDemonstration(Sentence instruction, List<Triplet<State,State,Boolean>> dataset) {
        this.instruction = instruction;
        this.dataset = dataset;
    }

    @Override
    public List<Triplet<State,State,Boolean>> getLabel() {
        return dataset;
    }

    @Override
    public boolean isCorrect(List<Triplet<State,State,Boolean>> listListPair) {
        return true;
    }

    @Override
    public Sentence getSample() {
        return instruction;
    }
}
