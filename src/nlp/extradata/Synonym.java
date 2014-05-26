/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.util.*;
import nlp.util.IOUtil;

/**
 * Từ điển từ đồng nghĩa (:) và trái nghĩa (::)
 *
 * @author Trung
 */
public class Synonym {

    public static Map<String, String> synonymMap;
    static final String filename = "data/VNsynonym.txt";

    public static boolean isSynonym(String str) {
        return synonymMap.containsKey(str.toLowerCase());
    }

    public static String[] getSynonyms(String str) {
        String tmp = synonymMap.get(str.toLowerCase());
        if (tmp != null) {
            String[] array = tmp.split(",");
            return array;
        } else {
            return new String[]{};
        }
    }

    public static void Init() {
        if (synonymMap == null) {
            synonymMap = new HashMap<>();
            ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
            for (String line : lines) {
                if (!line.contains("::")) {     // không trái nghĩa
                    String[] tokens = line.split("\\:");
                    synonymMap.put(tokens[0], tokens[1]);
                }
            }
        }
    }
}
