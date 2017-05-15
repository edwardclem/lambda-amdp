package lambdas.LambdaInterface;

import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.state.State;
import amdp.cleanup.state.CleanupState;
import jscheme.JScheme;
import jscheme.SchemeProcedure;


/**
 * Created by edwardwilliams on 5/15/17.
 *
 * State condition test based on lambda calculus prop function
 */
public class LambdaSC implements StateConditionTest{
    protected SchemeProcedure lambdaProp;
    protected JScheme jsEv; //stores jscheme evaluator

    public LambdaSC(SchemeProcedure lp, JScheme js){
        this.lambdaProp = lp;
        this.jsEv = js;
    }

    public boolean satisfies(State s){

        return (Boolean)jsEv.call(this.lambdaProp, (CleanupState)s);

    }

}
