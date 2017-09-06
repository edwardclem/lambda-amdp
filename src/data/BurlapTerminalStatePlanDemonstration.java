package data;

import burlap.behavior.singleagent.Episode;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Type that holds a sentence and plans associated with the sentence
 */

public class BurlapTerminalStatePlanDemonstration implements ILabeledDataItem<Sentence, List<Episode>> {

    private Sentence instruction;
    private List<Episode> dataset;

    public BurlapTerminalStatePlanDemonstration(Sentence instruction, List<Episode> dataset) {
        this.instruction = instruction;
        this.dataset = dataset;
    }

    @Override
    public List<Episode> getLabel() {
        return dataset;
    }

    @Override
    public boolean isCorrect(List<Episode> listListPair) {
        return true;
    }

    @Override
    public Sentence getSample() {
        return instruction;
    }

    public static BurlapTerminalStatePlanDemonstration parse(String s) {

        final String[] split = s.split("\n");
        int startPos = 0;
        if(split[0].equals("")){
            startPos=1;
        }
        Sentence sentence = new Sentence(split[startPos]);
        List<Episode> items = new ArrayList<>();
        for(int i = startPos+1; i < split.length ; i = i + 5) {
//            System.out.println(split[i+3]);
            Episode item = Episode.read(split[i + 3]);
            items.add(item);
        }
        return new BurlapTerminalStatePlanDemonstration(sentence,items);
    }

    public void addInstance(Episode instance) {
        dataset.add(instance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(instruction.toString()).append("\n");
        for(int i = 0; i < dataset.size(); i++) {
            Episode t = dataset.get(i);
            sb.append(DataHelpers.ooStateToStringCompact((OOState)(t.stateSequence.get(0)))).append("\n");
            sb.append(DataHelpers.ooStateToStringCompact((OOState)(t.stateSequence.get(t.stateSequence.size()-1))));
//            sb.append(t.stateSequence.get(t.stateSequence.size()-1).toString());
            if(i < dataset.size() - 1) {
                sb.append("\n---\n");
            }
        }
        return sb.toString();
    }
}
