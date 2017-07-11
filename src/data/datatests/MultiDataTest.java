package data.datatests;

import data.BurlapMultiDemonstrationDataset;

import java.io.File;
import java.io.IOException;

public class MultiDataTest {

    public static void main(String[] args) {
        File testData = new File("data/amt/amt_test_1_multi/test.bdm");
        try {
            BurlapMultiDemonstrationDataset test = BurlapMultiDemonstrationDataset.readFromFile(testData);
        } catch(IOException i) {
            i.printStackTrace();
        }
    }
}
