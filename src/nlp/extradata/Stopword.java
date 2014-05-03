/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class Stopword {

    private final Set<String> stopwords;
    private final String filename = "data/VNstopwords.txt";

    public Stopword() {
        ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
        stopwords = new HashSet<>(lines);
    }

    public boolean isStopWord(String s) {
        return stopwords.contains(s);
    }

    public static void main(String[] args) {
        //System.out.println(checkStopWord("xoẹt"));
        Stopword stopword = new Stopword();
        //stopword.getStopWords();
        System.out.println(stopword.isStopWord("xoẹt"));

//        Locale vnLocale = new Locale("vi", "VN");
//        Locale.setDefault(vnLocale);
//        Collator vnCollator = Collator.getInstance();
//        System.out.println(vnCollator.compare("a", "bá"));
//        System.out.println("xoẹt".compareTo("xa_xả"));
//        System.out.println("xoẹt".compareTo("xăm_xăm"));
//        System.out.println("xoẹt".compareTo("xềnh_xệch"));
    }

}
