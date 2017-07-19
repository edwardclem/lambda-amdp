package data;

import amdp.cleanup.state.CleanupState;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.utils.composites.Pair;
import edu.cornell.cs.nlp.utils.composites.Triplet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import burlap.mdp.core.state.State;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by edwardwilliams on 7/7/17.
 */
public class AMTConverter {
    /**
     * Loads all demonstrations from a CSV file. Creates MultiDemonstration
     * @param file_location
     * @return
     * @throws IOException
     */
    public static List<String> loadDemonstrationsFromCSV(String file_location) throws IOException{
        File csvData = new File(file_location);
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180.withFirstRecordAsHeader());

            ArrayList<String> allDemonstrations = new ArrayList<>();

            for (CSVRecord rec: parser){
                Map<String, String> recordMap = rec.toMap();

                allDemonstrations.add(recordToMultiDemonstration(recordMap, 3));

            }

        return allDemonstrations;

    }

    public static List<String> loadMultiDemonstrationFromReverseCSV(String file) throws IOException {
        File csvData = new File(file);
        CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180.withFirstRecordAsHeader());

        Map<String, BurlapMultiDemonstration> demoMap = new HashMap<>();

        for(CSVRecord rec: parser) {
            Map<String,String> recMap = rec.toMap();
            List<BurlapMultiDemonstration> demosFromRec = getMultiFromReverseCSV(recMap,3);
            for(BurlapMultiDemonstration bmd : demosFromRec) {
                if(demoMap.containsKey(bmd.getSample().getString())) {
                    BurlapMultiDemonstration demo = demoMap.get(bmd.getSample().getString());
                    for(Triplet<State,State,Boolean> instance : bmd.getLabel()) {
                        demo.addInstance(instance);
                    }
                } else {
                    demoMap.put(bmd.getSample().getString(),bmd);
                }
            }
        }

        List<String> allDemos = new ArrayList<>();

        for(BurlapMultiDemonstration bdm : demoMap.values()) {
            allDemos.add(bdm.toString());
        }

        return allDemos;
    }

    /**
     * Get a CleanupState from a URL
     * @param loc
     * @return
     */
    public static CleanupState URLToState(String loc) throws IOException{
        if(loc.equals("http://cs.brown.edu/~ngopalan/groundingImages/imagesForDataCollection/TakeTheBlockToTheBlueRoom/2/pre.txt")) {
            String stateString = "{\n" +
                    "agent0 (agent): {\n" +
                    "x: {2}\n" +
                    "y: {2}\n" +
                    "direction: {south}\n" +
                    "}\n" +
                    "door0 (door): {\n" +
                    "locked: {0}\n" +
                    "top: {4}\n" +
                    "left: {6}\n" +
                    "bottom: {4}\n" +
                    "right: {6}\n" +
                    "canBeLocked: {false}\n" +
                    "}\n" +
                    "door1 (door): {\n" +
                    "locked: {0}\n" +
                    "top: {4}\n" +
                    "left: {2}\n" +
                    "bottom: {4}\n" +
                    "right: {2}\n" +
                    "canBeLocked: {false}\n" +
                    "}\n" +
                    "block0 (block): {\n" +
                    "x: {6}\n" +
                    "y: {2}\n" +
                    "shape: {chair}\n" +
                    "colour: {blue}\n" +
                    "}\n" +
                    "room0 (room): {\n" +
                    "top: {4}\n" +
                    "left: {0}\n" +
                    "bottom: {0}\n" +
                    "right: {4}\n" +
                    "colour: {green}\n" +
                    "}\n" +
                    "room1 (room): {\n" +
                    "top: {4}\n" +
                    "left: {4}\n" +
                    "bottom: {0}\n" +
                    "right: {8}\n" +
                    "colour: {red}\n" +
                    "}\n" +
                    "room2 (room): {\n" +
                    "top: {8}\n" +
                    "left: {0}\n" +
                    "bottom: {4}\n" +
                    "right: {8}\n" +
                    "colour: {blue}\n" +
                    "}\n" +
                    "}";
            return DataHelpers.loadStateFromString(stateString);
        }
        else {
            URL url = new URL(loc);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder beforeStateString1 = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                beforeStateString1.append(str + "\n");
            }

            in.close();

            return DataHelpers.loadStateFromString(beforeStateString1.toString());
        }
    }


    /**
     * Converts a CSV record to a list of natural language commands paired with pre- and post- conditions
     * each language command paired with numPairs pairs of states
     * @param record
     * @param numPairs
     * @return
     */
    public static List<String> recordToDemonstrationList(Map<String, String> record, Integer numPairs){
        ArrayList<String> demonstrationList = new ArrayList<>(3);
        String command = record.get("Answer.command").replace(".", ""); //removing punctuation
        for (int i = 1; i<= numPairs; i++){
            String beforeImg = record.get("Input.before" + Integer.toString(i));
            String beforeFile = beforeImg.replaceAll("png", "txt"); //get corresponding state file

            String afterImg = record.get("Input.after" + Integer.toString(i));
            String afterFile = afterImg.replaceAll("png", "txt");

            //load Burlap states
            try{
                CleanupState beforeState = URLToState(beforeFile);
                CleanupState afterState = URLToState(afterFile);

                String appended = command + "\n" +
                        DataHelpers.ooStateToStringCompact(beforeState) + "\n" +
                        DataHelpers.ooStateToStringCompact(afterState);

                demonstrationList.add(appended);

            } catch(IOException e){
                e.printStackTrace();
            }

        }

        return demonstrationList;
    }

    public static List<BurlapMultiDemonstration> getMultiFromReverseCSV(Map<String, String> record, int recnum) {
        List<BurlapMultiDemonstration> recordDemos = new ArrayList<>(recnum);

        for(int i = 1; i <= recnum; i++) {
            Sentence instruction = new Sentence(record.get("Input.command" + i));
            try {
                State beforeState = URLToState(record.get("Input.before" + i).replaceAll("png","txt"));
                State afterState = URLToState(record.get("Input.after" + i).replaceAll("png","txt"));
                Boolean isTrue = Boolean.valueOf(record.get("Answer.Q" + i + "Answer"));
                List<Triplet<State,State,Boolean>> tList = new ArrayList<>(1);
                tList.add(new Triplet<>(beforeState,afterState,isTrue));
                recordDemos.add(new BurlapMultiDemonstration(instruction, tList));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recordDemos;
    }

    public static String recordToMultiDemonstration(Map<String, String> record, Integer numPairs){
        ArrayList<String> demonstrationList = new ArrayList<>(3);
        String command = record.get("Answer.command").replace(".", ""); //removing punctuation
        for (int i = 1; i<= numPairs; i++){
            String beforeImg = record.get("Input.before" + Integer.toString(i));
            String beforeFile = beforeImg.replaceAll("png", "txt"); //get corresponding state file

            String afterImg = record.get("Input.after" + Integer.toString(i));
            String afterFile = afterImg.replaceAll("png", "txt");

            //load Burlap states
            try{
                CleanupState beforeState = URLToState(beforeFile);
                CleanupState afterState = URLToState(afterFile);

                //combine pre- and post- condition states
                String appended = DataHelpers.ooStateToStringCompact(beforeState) + "\n" +
                        DataHelpers.ooStateToStringCompact(afterState) + "\n" + "true";

                demonstrationList.add(appended);

            } catch(IOException e){
                e.printStackTrace();
            }

        }

        String multiDemonstration = command + "\n" + String.join("\n---\n", demonstrationList);

        return multiDemonstration;
    }



    public static void saveDemonstrations(String filename, List<String> demonstrations) throws IOException{

        String allDemonstrations = String.join("\n\n", demonstrations);
        FileUtils.writeStringToFile(new File(filename), allDemonstrations, Charset.defaultCharset());

    }

    /**
     * Produce a train/test split of the loaded data.
     * @param items
     * @return
     */
    public static Pair<List<String>, List<String>> trainTestSplit(List<String> items, double train_pct){

        long seed = System.nanoTime();

        //randomly shuffle
        Collections.shuffle(items);

        int num_train = (int)(items.size()*train_pct);


        List<String> train = items.subList(0, num_train);

        List<String> test = items.subList(num_train, items.size());

        Pair<List<String>, List<String>> split = Pair.of(train, test);

        return split;
    }


    public static void main(String[] args){
        //TODO: exception handling
        String file_location = args[0];
        file_location = "data/amt/amt_test_3.csv";
        try{
            List<String> demonstrations = loadDemonstrationsFromCSV(file_location);

            Pair<List<String>, List<String>> split = trainTestSplit(demonstrations, 0.8);

            String fileRoot = "data/amt/amt_test_3";

            String trainFilename = fileRoot + "/train.bdm";

            String testFileName = fileRoot + "/test.bdm";

            saveDemonstrations(trainFilename, split.first());
            saveDemonstrations(testFileName, split.second());

        } catch(IOException e){
            e.printStackTrace();
        }


    }


}
