/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.util.*;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class Synonym {

    public Map<String, String> synonymMap = new HashMap<>();
    private final String filename = "data/VNsynonym.txt";

    public boolean isSynonym(String str) {
        return synonymMap.containsKey(str.toLowerCase());
    }

    public String[] getSynonyms(String str) {
        String tmp = synonymMap.get(str.toLowerCase());
        String[] array = tmp.split(",");
        return array;
    }

    public Synonym() {
        ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
        for (String line : lines) {
            if (!line.contains("::")) {
                String[] tokens = line.split("\\:");
                synonymMap.put(tokens[0], tokens[1]);
            }
        }
    }
}
