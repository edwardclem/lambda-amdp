package lambdas.LambdaTests;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import data.DataHelpers;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import lambdas.LambdaFunctions.Determiners;
import lambdas.LambdaPlanning.LambdaConverter;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestLambdaOnStates {

    public static void main(String[] args){

        //String precond = "{agent0 (agent): [x: {2} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {2} left: {4} bottom: {2} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {6} left: {4} bottom: {6} right: {4} canBeLocked: {false} ] , block0 (block): [x: {6} y: {2} shape: {chair} colour: {blue} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {red} ] , room1 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {green} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {blue} ] , }";
       //String precond = "{agent0 (agent): [x: {4} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {4} right: {8} colour: {green} ] , room1 (room): [top: {4} left: {0} bottom: {0} right: {4} colour: {blue} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {red} ] , }";
        //String postcond = "{agent0 (agent): [x: {4} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {2} left: {4} bottom: {2} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {6} left: {4} bottom: {6} right: {4} canBeLocked: {false} ] , block0 (block): [x: {3} y: {2} shape: {chair} colour: {blue} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {red} ] , room1 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {green} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {blue} ] , }";



        //String parse = "(near:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (argmin:<<e,t>,<<e,n>,e>> (lambda $1:e (block:<e,t> $1)) (lambda $2:e (size:<e,n> $2))))";


//        String precond = "{agent0 (agent): [x: {3} y: {15} direction: {south} ] , door0 (door): [locked: {0} top: {8} left: {12} bottom: {8} right: {12} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {8} left: {4} bottom: {8} right: {4} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , block1 (block): [x: {2} y: {3} shape: {chair} colour: {blue} ] , block3 (block): [x: {3} y: {10} shape: {chair} colour: {green} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {16} colour: {red} ] , room1 (room): [top: {16} left: {0} bottom: {8} right: {8} colour: {green} ] , room2 (room): [top: {16} left: {8} bottom: {8} right: {16} colour: {blue} ] , }";
//        String postcond = "{agent0 (agent): [x: {12} y: {10} direction: {north} ] , door0 (door): [locked: {0} top: {8} left: {12} bottom: {8} right: {12} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {8} left: {4} bottom: {8} right: {4} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , block1 (block): [x: {12} y: {11} shape: {chair} colour: {blue} ] , block3 (block): [x: {3} y: {10} shape: {chair} colour: {green} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {16} colour: {red} ] , room1 (room): [top: {16} left: {0} bottom: {8} right: {8} colour: {green} ] , room2 (room): [top: {16} left: {8} bottom: {8} right: {16} colour: {blue} ] , }";

//        String precond = "{agent0 (agent): [x: {6} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {2} left: {4} bottom: {2} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {5} left: {4} bottom: {5} right: {4} canBeLocked: {false} ] , block0 (block): [x: {5} y: {6} shape: {chair} colour: {red} ] , block1 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {green} ] , room1 (room): [top: {3} left: {4} bottom: {0} right: {8} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {green} ] , }";
//        String postcond = "{agent0 (agent): [x: {6} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {2} left: {4} bottom: {2} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {5} left: {4} bottom: {5} right: {4} canBeLocked: {false} ] , block0 (block): [x: {5} y: {6} shape: {chair} colour: {red} ] , block1 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {green} ] , room1 (room): [top: {3} left: {4} bottom: {0} right: {8} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {green} ] , }";
//

        String precond = "{agent0 (agent): [x: {6} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {3} left: {4} bottom: {3} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {5} left: {4} bottom: {5} right: {4} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {green} ] , room1 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }\n";

        String postcond = "{agent0 (agent): [x: {3} y: {5} direction: {south} ] , door0 (door): [locked: {0} top: {3} left: {4} bottom: {3} right: {4} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {5} left: {4} bottom: {5} right: {4} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {4} colour: {green} ] , room1 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }\n";


        //String parse = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (block:<e,t> $0))) (argmax:<<e,t>,<<e,n>,e>> (lambda $1:e (and:<t*,t> (blue:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1))) (lambda $2:e (size:<e,n> $2))))";

//        String parse = "(near:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (argmin:<<e,t>,<<e,n>,e>> (lambda $1:e (and:<t*,t> (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1))) (lambda $2:e (size:<e,n> $2))))";

        //String parse = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (green:<e,t> $2)) (lambda $3:e (size:<e,n> $3)))))))";

        String parse = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (block:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmin:<<e,t>,<<e,n>,e>> (lambda $2:e (and:<t*,t> (eq:<e,<e,t>> $2 (argmax:<<e,t>,<<e,n>,e>> (lambda $3:e (green:<e,t> $3)) (lambda $4:e (size:<e,n> $4)))) (in:<e,<e,t>> (the:<<e,t>,e> (lambda $5:e (room:<e,t> $5))) $2))) (lambda $6:e (size:<e,n> $6)))))))";

        OOState pre = DataHelpers.loadStateFromStringCompact(precond);
        OOState post = DataHelpers.loadStateFromStringCompact(postcond);

        String preds = "src/CleanupPredicatesJava.scm";

        JScheme js = new JScheme();
        try {
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

            String pred = LambdaConverter.convert(parse);

            js.setGlobalValue("initialState", pre);
            js.setGlobalValue("finalState", post);
            //NOTE:using strict definite determiner
            js.eval("(define the (definiteDeterminer initialState))");
            //define argmin and argmax wrt initial state
            js.eval("(define argmax (argmaxState initialState))");
            js.eval("(define argmin (argminState initialState))");

            SchemeProcedure pf = (SchemeProcedure)js.eval(pred);
            Boolean trueOnPost = (boolean) js.call(pf,post);

            Boolean falseOnPre = !(boolean) js.call(pf,pre);

            //System.out.println(Determiners.argmax(pre, (SchemeProcedure)js.eval("(lambda $3 (size $3))"), (SchemeProcedure)js.eval("(lambda $2 (green $2))")));


            System.out.println(falseOnPre);
            System.out.println(trueOnPost);

            //System.out.println(js.eval(LambdaConverter.convert("(argmin:<<e,t>,<<e,n>,e>> (lambda $1:e (and:<t*,t> (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1) (room:<e,t> $1))) (lambda $2:e (size:<e,n> $2)))")));




        } catch(FileNotFoundException e){
            System.out.print(e);
        }

    }
}
