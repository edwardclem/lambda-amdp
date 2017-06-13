package parsing;

import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by edwardwilliams on 6/13/17.
 * Derived from the GeoQuery SPF code in the Cornell SPF library.
 */
public class SPFMain {
    public static final ILogger	LOG	= LoggerFactory.create(BurlapParsingExp.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            LOG.error("Missing arguments. Expects a .exp file as argument.");
            System.exit(-1);
        }

        run(args[0]);
    }

    public static void run(String filename) {
        try {
            new BurlapParsingExp(new File(filename)).start();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


}
