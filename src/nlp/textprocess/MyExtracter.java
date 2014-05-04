/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Lớp trích rút thông tin, xếp hạng theo tf-isf và loại bỏ câu không quan
 * trọng, câu giống nhau... Xem xét rút gọn câu.
 *
 * @author Trung
 */
public class MyExtracter {

    final double REMAIN_RATE = 2.0 / 3;     // keep only 2/3 important sentences
    final double THRESHOLD = 0.7;
    final double TOP_K_KEYWORD = 0.15;

//    NounAnaphora na;
    private final ArrayList<MySentence> sentences;

    public MyExtracter(ArrayList<MyToken> data) {
        sentences = MySentence.DatumToSentence(data);
//        na = new NounAnaphora();
    }

    /**
     * Loại bỏ các câu gần giống nhau dựa trên độ đo similarity.
     *
     */
    void redundancing() {
        System.out.println("Start of redundancy...");
        double topI, bottomI, topJ, bottomJ;
        int nSentences = sentences.size();
        for (int i = 0; i < nSentences - 1; i++) {
            for (int j = i + 1; j < nSentences; j++) {
                MySentence sen_i = sentences.get(i);
                MySentence sen_j = sentences.get(j);
                if (Math.abs(sen_i.getScore() - sen_j.getScore()) < 0.2) {
                    topI = topJ = bottomI = bottomJ = 0;
                    for (MyToken di : sen_i.tokensList) {
                        bottomI += di.idf;
                        for (MyToken dj : sen_j.tokensList) {
                            if (dj.equals(di)) {
                                topI += di.idf;
                                break;
                            }
                        }
                    }
                    for (MyToken dj : sen_j.tokensList) {
                        bottomJ += dj.idf;
                        for (MyToken di : sen_i.tokensList) {
                            if (di.equals(dj)) {
                                topJ += dj.idf;
                                break;
                            }
                        }
                    }

                    double simScore = (topI / bottomI + topJ / bottomJ) / 2;
                    if (simScore > THRESHOLD) {
                        // xóa câu dài hơn
                        if (sen_i.tokensList.size() >= sen_j.tokensList.size()) {
                            sen_i.isRemove = true;
                        } else {
                            sen_j.isRemove = true;
                        }
                    }
                }
            }
        }

        // remove redundant sentences
        int i = 0;
        while (i < sentences.size()) {
            if (sentences.get(i).isRemove) {
                sentences.remove(i);
                System.out.println("Remove sentence " + i);
            } else {
                i++;
            }
        }
        System.out.println("End of redundancy...");
    }

    /**
     * Setup mapSenOrderByScore, keep only 2/3 important sentences.
     */
    void scoring() {
        for (MySentence sen_i : sentences) {
            for (MySentence sen_j : sentences) {
                if (sen_j.getScore() > sen_i.getScore()) {
                    sen_i.rank++;       // bao nhiêu câu có score cao hơn sen_i
                }
            }
        }

//        /// print important sentence
//        for (int i = 0; i < 3; i++) {
//            for (MySentence sentence : sentences) {
//                if (sentence.rank == i) {
//                    System.out.println(sentence.toString());
//                }
//            }
//        }
    }

    /**
     * Determine keywords in data list by set d.importance=true
     *
     */
    void setKeywords() {
        System.out.println("Start word-importance set...");
        TreeSet<Double> set = new TreeSet<>();
        int counter = 0;
        for (MySentence sentence : sentences) {
            for (MyToken token : sentence.tokensList) {
                set.add(token.tf_isf);
                counter++;
            }
        }

        final int nKeywords = (int) (TOP_K_KEYWORD * counter);
        boolean flag = true;
        counter = 0;
        while (flag) {
            Double maxScore = set.pollLast();
            for (MySentence sentence : sentences) {
                for (MyToken token : sentence.tokensList) {
                    if (maxScore == token.tf_isf) {
                        token.importance = flag;
                        if (flag) {
                            System.out.println(token.word);
                        }
                        counter++;
                        if (counter >= nKeywords) {
                            flag = false;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("End word-importance set...");
    }

    /**
     * Phân giải đồng tham chiếu; loại câu trùng lặp, dư thừa; xếp hạng câu.
     *
     * @return list các câu đã được extract
     */
    public ArrayList<MySentence> extract() {
//        na.nounAnaphoring(sentences);
        redundancing();
        setKeywords();
        scoring();

        return sentences;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MyToken> tokens = tokenizer.createTokens("corpus/Plaintext/1.txt");
        MyExtracter se = new MyExtracter(tokens);
//        se.setKeywords();
        se.scoring();
//        ArrayList<MySentence> sentences = se.extract();
//        for (MySentence sentence : sentences) {
//            System.out.println(sentence.toString());
//        }
//        
//        Set<Integer> set = new TreeSet<>();
//        set.add(3);
//        set.add(1);
//        set.add(2);
//        System.out.println(set);
    }
}
