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
            double idf = Double.parseDouble(tokens[1]);
            idfScoreMap.put(tokens[0], idf);
        }
    }

    /**
     * Tính chỉ số tf-isf.
     *
     * @param tokens
     */
    public void tf_isf(ArrayList<MyToken> tokens) {
        System.out.println("Start of isf-scoring...");
        int S = tokens.get(tokens.size() - 1).iSentence + 1;      // the total number of sentences in the document
        HashMap<String, int[]> tf_map = new HashMap<>();       // map có key là từ, value là mảng S+1 giá trị, với giá trị cuối lưu sum(arr)
        for (int i = 0; i < tokens.size(); i++) {
            MyToken di = tokens.get(i);
            int[] arr_tf;
            String key = di.word + "#" + di.posTag;
            if (tf_map.containsKey(key)) {
                arr_tf = tf_map.get(key);
                arr_tf[di.iSentence]++;
            } else {
                arr_tf = new int[S + 1];
                arr_tf[di.iSentence] = 1;
            }
            arr_tf[S]++;
            tf_map.put(key, arr_tf);
        }
        for (MyToken token : tokens) {
            if (token.word.length() <= 5 || token.punctuation || token.stopWord || token.semiStopWord) {
                continue;
            }
            int[] arr_tf = tf_map.get(token.word + "#" + token.posTag);
            int tf = arr_tf[token.iSentence];
            double isf = Math.log10(S / (arr_tf[S] + 0.0));
            token.tf_isf = tf * isf;
        }
        System.out.println("End of isf-scoring...\n");

        //////////////////////////////////////////////////////////////////////////
        System.out.println("Start of idf-scoring...");
        for (MyToken ti : tokens) {
            if (ti.punctuation || ti.stopWord || ti.semiStopWord) {
                continue;
            }
            if (ti.tf == 0) {
                int count = 1;
                int i = tokens.indexOf(ti);
                for (int j = i + 1; j < tokens.size(); j++) {
                    MyToken tj = tokens.get(j);
                    if (ti.equals(tj)) {
                        count++;
                        tj.tf = -i;     /// lưu lại chỉ số của thằng giống nó đã tính ở trước
                    }
                }
                ti.tf = count;
            } else if (ti.tf < 0) {
                ti.tf = tokens.get(-ti.tf).tf;      /// chỉ số được lưu dùng ở đây
            }

            if (idfScoreMap.containsKey(ti.word)) {
                ti.idf = idfScoreMap.get(ti.word);
            } else {
                ti.idf = Math.log(21628.0);        /// !!!
            }

            ti.tf_idf = ti.idf * ti.tf;
        }
        System.out.println("End of idf-scoring...\n");
    }

    public static void main(String[] args) throws IOException {
        IdfScore is = new IdfScore();
        System.out.println(is.idfScoreMap.get("không"));
    }
}
