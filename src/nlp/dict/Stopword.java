/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.dict;

import java.util.ArrayList;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class Stopword {

    private final ArrayList<String> stopwordList;
    private final String filename = "train-data/VNstopwords.txt";

    public ArrayList<String> getStopwordList() {
        return stopwordList;
    }

    public Stopword() {
        this.stopwordList = IOUtil.ReadFileByLine(filename);
    }

    public boolean isStopWord(String s) {
//        String s1 = s.toLowerCase();
        for (String sw : stopwordList) {
            if (s.equals(sw)) {
                return true;
            }
        }
        return false;
//        int index = Collections.binarySearch(stopwordList, s1);
//        return index;
    }

    public static void main(String[] args) {
        //System.out.println(checkStopWord("xoẹt"));
        Stopword stopword = new Stopword();
        //stopword.getStopWords();
        System.out.println(stopword.isStopWord("xoet"));

//        Locale vnLocale = new Locale("vi", "VN");
//        Locale.setDefault(vnLocale);
//        Collator vnCollator = Collator.getInstance();
//        System.out.println(vnCollator.compare("a", "bá"));
//        System.out.println("xoẹt".compareTo("xa_xả"));
//        System.out.println("xoẹt".compareTo("xăm_xăm"));
//        System.out.println("xoẹt".compareTo("xềnh_xệch"));
    }

}
