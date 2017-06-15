package lambdas.LambdaPlanning;

/**
 * Created by mrhee on 6/15/17.
 */
public class LambdaConverter {

    public static String convert(String command){
        StringBuilder schemeCommand = new StringBuilder();

        //take out typings
        String[] parsed = command.split("");
        boolean  typing = false;
        for(String c : parsed) {
            if(c.equals(":")) {
                typing = true;
            } else {
                if(typing && c.equals(" ")) {
                    typing =  false;
                }
                if(!typing)  {
                    schemeCommand.append(c);
                }
            }
        }

        //add parens to arguments
        int lindex = schemeCommand.indexOf("lambda");
        while(lindex != -1) {
            schemeCommand.insert(lindex + 7, "(");
            schemeCommand.insert(schemeCommand.indexOf(" ", lindex + 7), ")");
            lindex = schemeCommand.indexOf("lambda", lindex + 1);
        }


        return  schemeCommand.toString();
    }

    public static void main(String[] args) {
        String parsedCommand = "(the:<<e,t>,e> (lambda $0:e (block:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (red:<e,t> $1) (room:<e,t> $1)))))";
        String schemeCommand = convert(parsedCommand);
        System.out.println(schemeCommand);
    }
}
