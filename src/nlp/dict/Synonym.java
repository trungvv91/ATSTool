/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.dict;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Trung
 */
public class Synonym {

    public Map<String, String> synonymMap = new HashMap<>();
    private final String filename = "train-data/VNsynonym.txt";

    public boolean isSynonym(String str) {
        return synonymMap.containsKey(str.toLowerCase());
    }

    public String[] getSynonyms(String str) {
        String tmp = synonymMap.get(str.toLowerCase());
        String[] array = tmp.split("\\,");
        return array;
    }

    public Synonym() {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(filename)))) {
            while ((line = br.readLine()) != null) {
                if (line.contains("::") == false) {
                    String[] tmp = line.split("\\:");
                    synonymMap.put(tmp[0], tmp[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Synonym.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Synonym.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
