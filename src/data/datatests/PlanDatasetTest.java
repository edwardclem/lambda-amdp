package data.datatests;

import data.BurlapTerminalStatePlanDataset;

import java.io.File;
import java.io.IOException;

public class PlanDatasetTest {
    public static void main(String[] args) {
        File testData = new File("data/episodesFromDataTrain/BDMWithEpisodeFilePath.txt");
        try {
            BurlapTerminalStatePlanDataset test = BurlapTerminalStatePlanDataset.readFromFile(testData);
            System.out.print(test);
//            System.out.println("\n===============+++++++++++++++++++++++=====================+++++++++++++++++=====================\n");
        } catch(IOException i) {
            i.printStackTrace();
        }
    }
}
