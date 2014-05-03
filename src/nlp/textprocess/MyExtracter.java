/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nlp.data.NounAnaphora;
import nlp.util.QuickSort;

/**
 *
 * @author Trung
 */
public class MyExtracter {

    final double REMAIN_RATE = 2.0 / 3;
    final double THRESHOLD = 0.7;

    /**
     * <K, V> = <isenstence, order>
     */
    public Map<Integer, Integer> mapSenOrderByScore = new HashMap<>();
    NounAnaphora na = new NounAnaphora();
    ArrayList<MyToken> data;
    
    public static int[] getTopKSentence(ArrayList<MyToken> data, int k) {
        ArrayList<MySentence> sentences = MySentence.DatumToSentence(data);
        /// Set mapSenOrderByScore
        System.out.println("Start of sen-scoring");
        int nSentences = sentences.size();
        double[] senScore = new double[nSentences];
        int[] senIndex = new int[nSentences];
        for (int i = 0; i < nSentences; i++) {
            senIndex[i] = i;
            ArrayList<MyToken> sen_i = sentences.get(i).dataList;
            double score = 0;
            int W = 0;
            for (MyToken dt : sen_i) {
                if (!dt.stopWord) {
                    score += dt.tf_isf;
                    W++;
                }
            }
//            int nPhrases_i = sen_i.get(sen_i.size() - 1).iPhrase + 1;
            senScore[i] = score / W;
//            System.out.println("sen " + i + ": " + score + " - " + W);
        }
        QuickSort.QuickSort(senScore, senIndex, 0, nSentences - 1);

        // map tf_isf
        int[] topSenIndex = new int[k];
        System.arraycopy(senIndex, 0, topSenIndex, 0, k);

        System.out.println("End of sen-scoring...");
        return topSenIndex;
    }

    public MyExtracter(ArrayList<MyToken> data) {
        this.data = data;
    }

    /**
     * Loại bỏ các câu gần giống nhau. /// cần xem lại tham số là list Token hay
 list MySentence
     *
     */
    void redundancing() {
        System.out.println("Start of redundancy...");

        ArrayList<ArrayList<MyToken>> sentenceArray = MyToken.DatumToSentence(data);
        int numOfSen = sentenceArray.size();
        /**
         * Similarity score between two iSentence
         */
        double[][] senSim = new double[numOfSen][numOfSen];

        /**
         * array return whether iSentence have a longer length
         */
        int[][] senLeg = new int[numOfSen][numOfSen];
        String senRedun = "";
        int iLeg, jLeg;       // length of iSentence i,j
        double topI, bottomI, topJ, bottomJ;
        for (int i = 0; i < numOfSen - 1; i++) {
            for (int j = i + 1; j < numOfSen; j++) {
                iLeg = sentenceArray.get(i).size();
                jLeg = sentenceArray.get(j).size();
                senLeg[i][j] = iLeg >= jLeg ? i : j;        /// delete longer iSentence

                topI = topJ = bottomI = bottomJ = 0;
                for (MyToken di : sentenceArray.get(i)) {
                    bottomI += di.idf;
                    for (MyToken dj : sentenceArray.get(j)) {
                        if (dj.equals(di)) {        /// StringUtils.contains(iSentence.get(i), dj.word)
                            topI += di.idf;
                            break;
                        }
                    }
                }
                for (MyToken dj : sentenceArray.get(j)) {
                    bottomJ += dj.idf;
                    for (MyToken di : sentenceArray.get(j)) {
                        if (di.equals(dj)) {
                            topJ += dj.idf;
                            break;
                        }
                    }
                }
                senSim[i][j] = (topI / bottomI + topJ / bottomJ) / 2.0;
                if (senSim[i][j] > THRESHOLD) {
                    senRedun += senLeg[i][j] + ":";
                }
            }
        }

        if (!senRedun.equals("")) {
            String[] arrRedun = senRedun.split(":");
            for (int i = 0; i < arrRedun.length; i++) {
                sentenceArray.remove(Integer.parseInt(arrRedun[i]) - i);
            }
            System.out.println(arrRedun.length + " sentences are removed");
        }
        System.out.println("End of redundancy...");
    }

    /**
     * Setup mapSenOrderByScore, keep only 2/3 important sentences
     *
     * @return list các câu đã được extract
     */
    public ArrayList<MySentence> extract() {
        ArrayList<MySentence> sentences = MySentence.DatumToSentence(data);

//        na.nounAnaphoring(sentences);
//        redundancing(data);
        //
        /// Set mapSenOrderByScore
        System.out.println("Start of sen-scoring");
        int nSentences = sentences.size();
        double[] senScore = new double[nSentences];
        int[] senIndex = new int[nSentences];
        for (int i = 0; i < nSentences; i++) {
            senIndex[i] = i;
            ArrayList<MyToken> sen_i = sentences.get(i).dataList;
            double score = 0;
            int W = 0;
            for (MyToken dt : sen_i) {
                if (dt.tf_isf > 0) {
                    score += dt.tf_isf;
                    W++;
                }
            }
//            int nPhrases_i = sen_i.get(sen_i.size() - 1).iPhrase + 1;
            senScore[i] = score / W;
        }
        QuickSort.QuickSort(senScore, senIndex, 0, nSentences - 1);
        int nremains = (int) (nSentences * REMAIN_RATE) + 1;

        // map tf_isf
        int[] topSenIndex = new int[nremains];
        int[] topSenIndexTmp = new int[nremains];
        System.arraycopy(senIndex, 0, topSenIndex, 0, nremains);
        System.arraycopy(topSenIndex, 0, topSenIndexTmp, 0, nremains);
        Arrays.sort(topSenIndexTmp, 0, nremains);
        for (int i = 0; i < topSenIndexTmp.length; i++) {
            for (int j = 0; j < topSenIndex.length; j++) {
                if (topSenIndexTmp[i] == topSenIndex[j]) {
                    topSenIndex[j] = i;     /// update sentence index after removing
                }
            }
        }
        for (int i = 0; i < topSenIndex.length; i++) {
//            mapSenOrderByScore.put(topSenIndex[i], i);
            mapSenOrderByScore.put(i, topSenIndex[i]);
        }

        // Remove unimportant sentences
        Arrays.sort(senIndex, nremains, senIndex.length);
        for (int i = senIndex.length - 1; i >= nremains; i--) {
            sentences.remove(senIndex[i]);
            System.out.println("remove sentence " + senIndex[i]);
        }
        System.out.println(nremains + " sentences remained");

        System.out.println(mapSenOrderByScore.toString());
        System.out.println("End of sen-scoring...");

        return sentences;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MyToken> tokens = tokenizer.createTokens("corpus/Plaintext/1.txt");
        MyExtracter se = new MyExtracter(tokens);
        ArrayList<MySentence> sentences = se.extract();
        for (MySentence sentence : sentences) {
            System.out.println(sentence.toString());
        }
    }
}
