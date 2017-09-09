package experiments;

import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;
import experiments.explat.LambdaExperiment;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Created by edwardwilliams on 6/22/17.
 */
public class LambdaExpRunner {

    public static final ILogger LOG	= LoggerFactory.create(LambdaExpRunner.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            LOG.error("Missing arguments. Expects a .exp file as argument.");
            System.exit(-1);
        }
        try {
            System.out.println("here!!!");
            long start = System.currentTimeMillis();
            new LambdaExperiment(new File(args[0])).start();
            System.out.println("\t" + (System.currentTimeMillis() - start));
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }
    }
}
