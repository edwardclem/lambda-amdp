package validation;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import jscheme.JScheme;
import jscheme.SchemeProcedure;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.oo.OOSADomain;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import lambdas.LambdaPlanning.LambdaConverter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BurlapValidator implements IValidator<BurlapDemonstration, String> {

    private static JScheme js;
    private String preds = "src/CleanupPredicates.scm";

    public BurlapValidator() {
        js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }

    @Override
    public boolean isValid(BurlapDemonstration dataItem, String parse) {
        String pred = LambdaConverter.convert(parse);
        State initialState = dataItem.getSample().getState();
        js.setGlobalValue("initialState", (CleanupState) initialState);
        js.eval("(define the (satisfiesPredicate initialState))");
        State after = dataItem.getAfterState();
        SchemeProcedure pf = (SchemeProcedure)js.eval(pred);
        return (Boolean) js.call(pf, (CleanupState) after);
    }


    public static void main(String[] args) {

        String parsedCommand = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (block:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (green:<e,t> $1) (room:<e,t> $1)))))";
        State s = CleanupDomain.getClassicState(true);


        CleanupRoom r1 = new CleanupRoom("room0", 4, 0, 0, 8, "red");
        CleanupRoom r2 = new CleanupRoom("room1", 8, 0, 4, 4, "green");
        CleanupRoom r3 = new CleanupRoom("room2", 8, 4, 4, 8, "blue");
        List<CleanupRoom> rooms = new ArrayList();
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        CleanupDoor d1 = new CleanupDoor("door0", 0, 4, 6, 4, 6, false);
        CleanupDoor d2 = new CleanupDoor("door1", 0, 4, 2, 4, 2, false);
        List<CleanupDoor> doors = new ArrayList();
        doors.add(d1);
        doors.add(d2);
        CleanupAgent agent = new CleanupAgent("agent0", 2, 6);
        agent.directional = true;
        agent.currentDirection = "south";
        CleanupBlock block1 = new CleanupBlock("block0", 3, 6, "basket", "red");
        List<CleanupBlock> blocks = new ArrayList();
        blocks.add(block1);
        CleanupState afterState = new CleanupState(agent, blocks, doors, rooms);
        BurlapDemonstration demonstration = new BurlapDemonstration("put the block in the green room",s, afterState);
        BurlapValidator v = new BurlapValidator();
        System.out.println(v.isValid(demonstration, parsedCommand));
    }
}