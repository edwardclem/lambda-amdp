package data;
import amdp.cleanup.CleanupDomain;
import amdp.cleanup.CleanupVisualiser;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

public class CleanupPairVisualizer {

    public static void main(String[] args) {
        Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");

        String preStateString = "{agent0 (agent): [x: {5} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {green} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {blue} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

        State preState = DataHelpers.loadStateFromStringCompact(preStateString);
        OOSADomain domain = new CleanupDomain().generateDomain();
        VisualExplorer exp = new VisualExplorer(domain, v, preState);
        exp.initGUI();
    }
}
