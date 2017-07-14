package data;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.utils.composites.Triplet;

import java.util.ArrayList;
import java.util.List;

public class BurlapMultiDemonstration implements ILabeledDataItem<Sentence, List<Triplet<State,State,Boolean>>> {

    private Sentence instruction;
    private List<Triplet<State,State,Boolean>> dataset;

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

    public static BurlapMultiDemonstration parse(String s) {

        final String[] split = s.split("\n");
        Sentence sentence = new Sentence(split[0]);
        List<Triplet<State,State,Boolean>> items = new ArrayList<>();
        for(int i = 1; i < split.length ; i = i + 4) {
            State pre = DataHelpers.loadStateFromStringCompact(split[i]);
            State post = DataHelpers.loadStateFromStringCompact(split[i + 1]);
            Boolean isTrue = Boolean.parseBoolean(split[i + 2]);
            Triplet<State,State,Boolean> item = new Triplet<>(pre,post,isTrue);
            items.add(item);
        }
        return new BurlapMultiDemonstration(sentence,items);
    }

    public void addInstance(Triplet<State, State, Boolean> instance) {
        dataset.add(instance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(instruction.toString()).append("\n");
        for(int i = 0; i < dataset.size(); i++) {
            Triplet<State,State,Boolean> t = dataset.get(i);
            sb.append(DataHelpers.ooStateToStringCompact((OOState)t.first())).append("\n");
            sb.append(DataHelpers.ooStateToStringCompact((OOState)t.second())).append("\n");
            sb.append(t.third()).append("\n");
            if(i < dataset.size() - 1) {
                sb.append("---\n");
            }
        }
        return sb.toString();
    }
}
