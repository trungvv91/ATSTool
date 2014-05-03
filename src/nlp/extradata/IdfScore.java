/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nlp.textprocess.MyToken;
import nlp.util.IOUtil;

/**
 *
 * @author Trung
 */
public class IdfScore {

    final String filename = "data/idf_final.txt";
    Map<String, Double> idf_index;

    public IdfScore() {
        idf_index = new HashMap<>();
        ArrayList<String> lines = IOUtil.ReadFileByLine(filename);
        for (String line : lines) {
            String[] tokens = line.split(" ");
            double idf = Double.parseDouble(tokens[1]);
            idf_index.put(tokens[0], idf);
        }
    }

    private Map<String, Double> getIdfScoreMap(ArrayList<MyToken> tokens) {
        Map<String, Double> map = new HashMap<>();
        for (MyToken token : tokens) {
            String word = token.word;
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

    /**
     * Tính chỉ số tf-isf.
     *
     * @param tokens
     */
    public void tf_isf(ArrayList<MyToken> tokens) {
        System.out.println("Start of isf-scoring...");
        int S = tokens.get(tokens.size() - 1).iSentence + 1;      // the total number of sentences in the document
        Map<String, int[]> tf_map = new HashMap<>();       // map có key là từ, value là mảng S+1 giá trị, với giá trị cuối lưu sum(arr)
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
        for (MyToken datum : tokens) {
            if (datum.word.length() <= 5 || datum.punctuation || datum.stopWord) {
                continue;
            }
            int[] arr_tf = tf_map.get(datum.word + "#" + datum.posTag);
            int tf = arr_tf[datum.iSentence];
            double isf = Math.log10(S / (arr_tf[S] + 0.0));
            datum.tf_isf = tf * isf;
        }
        System.out.println("End of isf-scoring...\n");

        System.out.println("Start of idf-scoring...");        
        Map<String, Double> maps = getIdfScoreMap(tokens);
        for (MyToken di : tokens) {
            if (di.punctuation || di.stopWord) {
                continue;
            }
            if (di.tf == 0) {
                int count = 1;
                int i = tokens.indexOf(di);
                for (int j = i + 1; j < tokens.size(); j++) {
                    MyToken dj = tokens.get(j);
                    if (di.equals(dj)) {
                        count++;
                        dj.tf = -i;     /// lưu lại chỉ số của thằng giống nó đã tính ở trước
                    }
                }
                di.tf = count;
            } else if (di.tf < 0) {
                di.tf = tokens.get(-di.tf).tf;      /// chỉ số được lưu dùng ở đây
            }
            di.idf = maps.get(di.word);
            di.tf_idf = di.idf * di.tf;
        }
        System.out.println("End of idf-scoring...\n");
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
