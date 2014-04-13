/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.sentenceExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh Tien
 */
public class IdfScore {

    final String fileName = "train-data/idf_final.txt";
    Map<String, Double> idf_index;

    public IdfScore() {
        idf_index = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] word = line.split(" ");
                double idf = Double.parseDouble(word[1]);
                idf_index.put(word[0], idf);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IdfScore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IdfScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, Double> getIdfScoreMap(ArrayList<Datum> datums) {
        Map<String, Double> map = new HashMap<>();
        for (Datum datum : datums) {
            String word = datum.word.toLowerCase();
            double idf;
            if (idf_index.containsKey(word)) {
                idf = (double) idf_index.get(word);
            } else {
                idf = Math.log(21628.0);        /// !!!
            }
            map.put(word, idf);
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
//        IdfScore is = new IdfScore();
//        ArrayList<String> list = new ArrayList<>();
//        list.add("hello");
//        Map m;
//        m = is.getIdfScoreMap(list);
//        System.out.println(m.toString());
    }
}
