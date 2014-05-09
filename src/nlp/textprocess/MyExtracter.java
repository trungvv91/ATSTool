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

    final double REMAIN_RATE = 0.7;     // keep only 2/3 important sentences
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
                System.out.println("\nRemove sentence " + i);
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

        /// keep only important sentences
        int limRank = (int) (sentences.size() * REMAIN_RATE);
        for (int i = 0; i < sentences.size();) {
            if (sentences.get(i).rank < limRank) {
//                System.out.println(sentences.get(i).toString());
                i++;
            } else {
                System.out.println("\nRemove sentence " + i + ": " + sentences.get(i).toString());
                sentences.remove(i);
            }
        }

        // update iSentence
        for (int i = 0; i < sentences.size(); i++) {
            for (MyToken token : sentences.get(i).tokensList) {
                token.iSentence = i;
            }
        }
    }

    /**
     * Determine keywords in data list by set d.keyword=true
     *
     */
    void setKeywords() {
        System.out.println("Start word-importance set...");
        TreeSet<Double> set = new TreeSet<>();
        int counter = 0;

        double maxTfIsf = 0;
        double maxTfIdf = 0;
        for (MySentence sentence : sentences) {
            for (MyToken token : sentence.tokensList) {
                if (token.tf_isf > maxTfIsf) {
                    maxTfIsf = token.tf_isf;
                }
                if (token.tf_idf > maxTfIdf) {
                    maxTfIdf = token.tf_idf;
                }
            }
        }

        for (MySentence sentence : sentences) {
            for (MyToken token : sentence.tokensList) {
                if (token.tf_isf > 0) {
                    double score = (token.tf_isf / maxTfIsf + token.tf_idf / maxTfIdf) / 2;
                    set.add(score);
                    counter++;
                }
            }
        }

        final int nKeywords = (int) (TOP_K_KEYWORD * counter);
        boolean flag = true;
        counter = 0;
        String keys = "";
        while (flag) {
            Double maxScore = set.pollLast();
            for (MySentence sentence : sentences) {
                for (MyToken token : sentence.tokensList) {
                    double score = (token.tf_isf / maxTfIsf + token.tf_idf / maxTfIdf) / 2;
                    if (maxScore == score) {
//                        token.keyword = flag;
                        token.keyword = true;
//                        if (flag) {
//                            System.out.println(token.word);
//                        }
                        if (!keys.contains(token.word)) {
                            System.out.println(token.word);
                            keys += "#" + token.word + "#";
                            counter++;
                        }
//                        if (counter >= nKeywords) {
//                            flag = false;
//                            break;
//                        }
                    }
                }
            }
            flag = counter < nKeywords;
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

    public void reduct() {

    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MyToken> tokens = tokenizer.createTokens("corpus/Plaintext/1.txt");
        MyExtracter se = new MyExtracter(tokens);
        se.setKeywords();
//        se.scoring();
//        ArrayList<MySentence> sentences = se.extract();
//        for (MySentence sentence : sentences) {
//            System.out.println(sentence.toString());
//        }
    }
}
