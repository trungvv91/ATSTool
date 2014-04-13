/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.sentenceExtraction;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nlp.dict.NounAnaphora;
import nlp.graph.QuickSort;
import nlp.tool.vnTextPro.VNTagger;

/**
 *
 * @author Manh Tien
 */
public class SentenceExtraction {

    /**
     * <K, V> = <isenstence, order>
     */
    public Map<Integer, Integer> mapSenOrderByScore = new HashMap<>();
    public NounAnaphora na = new NounAnaphora();
    final double REMAIN_RATE = 2.0 / 3;

    /**
     * Loại bỏ các câu gần giống nhau
     *
     * @param datums
     * @throws IOException
     */
    public void SentenceRedundancing(List<Datum> datums) throws IOException {
        System.out.println("Start of redundancy...");

        final double THRESHOLD = 0.7;
        ArrayList<ArrayList<Datum>> sentenceArray = DatumUtil.DatumToSentence(datums);
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
                for (Datum di : sentenceArray.get(i)) {
                    bottomI += di.idf;
                    for (Datum dj : sentenceArray.get(j)) {
                        if (dj.equals(di)) {        /// StringUtils.contains(iSentence.get(i), dj.word)
                            topI += di.idf;
                            break;
                        }
                    }
                }
                for (Datum dj : sentenceArray.get(j)) {
                    bottomJ += dj.idf;
                    for (Datum di : sentenceArray.get(j)) {
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
     * @param inputNum - file name number
     * @param datums
     * @return tagged datums list
     * @throws IOException
     */
    public ArrayList<Sentence> extract(String inputNum, List<Datum> datums) throws IOException {
        ArrayList<Sentence> sentences = Sentence.DatumToSentence(datums);

//        na.nounAnaphoring(sentences);
//        SentenceRedundancing(datums);
        //
        /// Set mapSenOrderByScore
        System.out.println("Start of sen-scoring");
        int nSentences = sentences.size();
        double[] senScore = new double[nSentences];
        int[] senIndex = new int[nSentences];
        for (int i = 0; i < nSentences; i++) {
            senIndex[i] = i;
            ArrayList<Datum> sen_i = sentences.get(i).dataList;
            double tf = 0;
            for (Datum dt : sen_i) {
                if (!dt.stopWord) {
                    tf += dt.tf;
                }
            }
            int nPhrases_i = sen_i.get(sen_i.size() - 1).iPhrase + 1;
            senScore[i] = tf / nPhrases_i;
        }
        QuickSort.QuickSort(senScore, senIndex, 0, nSentences - 1);
        int nremains = (int) (nSentences * REMAIN_RATE) + 1;

        // Remove unimportant sentences 
        for (int i = senIndex.length - 1; i >= nremains; i--) {
            sentences.remove(senIndex[i]);
            System.out.println("remove sentence " + senIndex[i]);
        }
        System.out.println(nremains + " sentences remained");

        // 
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
        System.out.println(mapSenOrderByScore.toString());
        System.out.println("End of sen-scoring...");

        return sentences;
    }

    public static void main(String[] args) {
        VNTagger tagger = VNTagger.getInstance();
        List<Datum> datums;
        try {
            datums = tagger.tagger("1");
            SentenceExtraction se = new SentenceExtraction();
            ArrayList<Sentence> sentences = se.extract("1", datums);
//            for (Sentence sentence : sentences) {
//                System.out.println(sentence.toString());
//            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
