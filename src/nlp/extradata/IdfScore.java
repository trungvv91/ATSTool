/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import nlp.textprocess.MyToken;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class IdfScore {

    private final String filename = "data/idf_final.txt";
    private final HashMap<String, Double> idfScoreMap;

    public IdfScore() {
        idfScoreMap = new HashMap<>();
        ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
        for (String line : lines) {
            String[] tokens = line.split(" ");
            String word = tokens[0];
            double idf = Double.parseDouble(tokens[1]);
            idfScoreMap.put(word, idf);
        }
    }

    /**
     * Tính chỉ số tf-isf.
     *
     * @param tokens
     */
    public void tf_isf(ArrayList<MyToken> tokens) {
        System.out.println("Start of isf-scoring...");
        int S = tokens.get(tokens.size() - 1).nSentence + 1;      // the total number of sentences in the document
        HashMap<String, int[]> tf_map = new HashMap<>();       // map có key là từ, value là mảng S+1 giá trị, với giá trị cuối lưu số phần tử khác 0 của arr, hay số câu chứa w
        for (int i = 0; i < tokens.size(); i++) {
            MyToken ti = tokens.get(i);
            int[] arr_tf;
            String key = ti.word + "#" + ti.posTag;
            if (tf_map.containsKey(key)) {
                arr_tf = tf_map.get(key);
                if (arr_tf[ti.nSentence] == 0) {
                    arr_tf[S]++;
                }
                arr_tf[ti.nSentence]++;
            } else {
                arr_tf = new int[S + 1];
                arr_tf[ti.nSentence] = 1;
                arr_tf[S] = 1;
            }
            tf_map.put(key, arr_tf);
        }
        for (MyToken token : tokens) {
            if (token.word.length() <= 5 || token.punctuation || token.stopWord || token.semiStopWord) {
                continue;
            }
            int[] arr_tf = tf_map.get(token.word + "#" + token.posTag);
            int tf = arr_tf[token.nSentence];
            double isf = 1 + Math.log10(S / (arr_tf[S] + 0.0));
            token.tf_isf = tf * isf;
        }
        System.out.println("End of isf-scoring...\n");

        //////////////////////////////////////////////////////////////////////////
        System.out.println("Start of idf-scoring...");
        for (int i = 0; i < tokens.size(); i++) {
            MyToken ti = tokens.get(i);
            if (ti.punctuation || ti.stopWord || ti.semiStopWord) {
                continue;
            }
            int tf = 1;
            for (int j = i + 1; j < tokens.size(); j++) {
                MyToken tj = tokens.get(j);
                if (ti.equals(tj)) {
                    tf++;
                }
            }
            double idf = Math.log(21628.0);
            if (idfScoreMap.containsKey(ti.word)) {
                idf = idfScoreMap.get(ti.word);
            }
            ti.tf_idf = idf * tf;
        }
        System.out.println("End of idf-scoring...\n");
    }

    public static void main(String[] args) throws IOException {
        IdfScore is = new IdfScore();
        System.out.println(is.idfScoreMap.get("không"));
    }
}
