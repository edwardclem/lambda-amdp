package experiments.explat;

import edu.cornell.cs.nlp.spf.explat.DistributedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.ResourceCreatorRepository;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LogLevel;
import edu.cornell.cs.nlp.utils.log.Logger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by edwardwilliams on 6/22/17.
 */
public class LambdaExperiment extends DistributedExperiment {

    public static final ILogger LOGGER = LoggerFactory.create(LambdaExperiment.class);

    public LambdaExperiment(File expFile) throws IOException, SAXException{
        this(expFile,  Collections.<String, String> emptyMap(), new LambdaResourceRepo());
    }

    public LambdaExperiment(File expFile, Map<String, String> envParams, ResourceCreatorRepository repo) throws IOException, SAXException{
        super(expFile, envParams, repo);

        LogLevel.DEBUG.set();


        //TODO: logical form initialization + types + ontology


        //only testing data loading for now

        //Read resources
        for (final Parameters params : resourceParams) {
            final String type = params.get("type");
            final String id = params.get("id");
            if (getCreator(type) == null) {
                throw new IllegalArgumentException("Invalid resource type: "
                        + type);
            } else {
                storeResource(id, getCreator(type).create(params, this));
            }
        }

    }
}
