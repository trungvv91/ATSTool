/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.util.ArrayList;
import java.util.HashSet;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class Stopword {

    private final HashSet<String> stopwords;
    private final String filename = "data/VNstopwords.txt";

    public Stopword() {
        ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
        stopwords = new HashSet<>(lines);
    }

    public boolean isStopWord(String s) {
        return stopwords.contains(s);
    }

    public static void main(String[] args) {
        Stopword stopword = new Stopword();
        System.out.println(stopword.isStopWord("xoẹt"));
    }

}
