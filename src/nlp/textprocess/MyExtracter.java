/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import nlp.graph.WordsGraph;

/**
 * Lớp trích rút thông tin, xếp hạng theo tf-isf và loại bỏ câu không quan
 * trọng, câu giống nhau... Xem xét rút gọn câu.
 *
 * @author Trung
 */
public class MyExtracter {

//    NounAnaphora na;
    private final ArrayList<MySentence> sentences;

    public MyExtracter(ArrayList<MySentence> sentences) {
        this.sentences = sentences;
//        na = new NounAnaphora();
    }

    /**
     * udpate remove redundant sentences
     */
    void remove() {
        int i = 0;
        while (i < sentences.size()) {
            if (sentences.get(i).isRemove) {
                sentences.remove(i);
//                System.out.println("Remove sentence " + i);
            } else {
                i++;
            }
        }
    }

    /**
     * Loại bỏ các câu gần giống nhau dựa trên độ đo similarity.
     *
     */
    void redundancing() {
        final double SIM_THRESHOLD = 0.7;

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
                    if (simScore > SIM_THRESHOLD) {
//                        if (sen_i.tokensList.size() >= sen_j.tokensList.size()) { // xóa câu dài hơn
                        if (sen_i.getScore() < sen_j.getScore()) { // xóa câu rank thấp hơn
                            sen_i.isRemove = true;
                        } else {
                            sen_j.isRemove = true;
                        }
                    }
                }
            }
        }

        remove();
        System.out.println("End of redundancy...");
    }

    /**
     * Xếp hạng câu (sau khi đã loại bỏ redundant sentence)
     */
    void scoring() {
        System.out.println("Start of scoring...");
        for (MySentence sen_i : sentences) {
            for (MySentence sen_j : sentences) {
                if (sen_j.getScore() > sen_i.getScore()) {
                    sen_i.rank++;       // bao nhiêu câu có score cao hơn sen_i
                }
            }
        }
        System.out.println("End of scoring...");
    }

    /**
     * Phân giải đồng tham chiếu; loại câu trùng lặp, dư thừa; xếp hạng câu.
     *
     * @param maxWord số chữ (không phải số từ)
     * @return list các câu đã được extract
     */
    public ArrayList<MySentence> extract(int maxWord) {
//        na.nounAnaphoring(sentences);
        redundancing();
        scoring();

        int counter = 0;
        int rank = 0;
        for (MySentence sentence : sentences) {
            sentence.isRemove = true;
        }
        while (counter < maxWord) {
//            boolean flag = false;
            for (MySentence sentence : sentences) {
                if (sentence.rank == rank && sentence.tokensList.size() > 10) {
                    if (rank + 1 >= sentences.size() || maxWord - counter < sentence.tokensList.size()) {
                        counter = maxWord + 1;
                    } else {
//                    rank++;
//                    flag = true;
                        sentence.isRemove = false;
                        for (MyToken token : sentence.tokensList) {
                            if (!token.punctuation) {
                                counter += token.word.split("_").length;
                            }
                        }
//                    break;
                    }
                }
            }
//            if (flag) {
            rank++;
//            }
        }

        remove();
        return sentences;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/khoahoc_giaoduc/KHGD2.txt");
        MyReducer re = new MyReducer(sentences);
        WordsGraph wg = new WordsGraph(re.reduction());
        MyExtracter se = new MyExtracter(wg.combination());
        sentences = se.extract(120);
        for (MySentence mySentence : sentences) {
            System.out.println(mySentence.toString());
            System.out.println(mySentence.getScore() + " ; " + mySentence.rank);
        }
    }
}
