/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import nlp.extradata.Synonym;

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
//        System.out.println("Remove: ");
//        for (int i = 0; i < sentences.size(); i++) {
//            if (sentences.get(i).isRemove) {
//                System.out.print(i + " ");
//            }
//        }
//        System.out.println("------");

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
        final double SIM_THRESHOLD = 0.4;

        System.out.println("Start of redundancy...");
        double sim_i, all_i, sim_j, all_j;
        int nSentences = sentences.size();
        for (int i = 0; i < nSentences - 1; i++) {
            for (int j = i + 1; j < nSentences; j++) {
                MySentence sen_i = sentences.get(i);
                MySentence sen_j = sentences.get(j);
                if (Math.abs(sen_i.getScore() - sen_j.getScore()) < 0.2) {
                    sim_i = sim_j = all_i = all_j = 0;
                    for (MyToken di : sen_i.tokensList) {
                        all_i += di.tf_isf;
                        for (MyToken dj : sen_j.tokensList) {
                            if (dj.equals(di) || dj.isSimilarTo(di)) {
                                sim_i += di.tf_isf;
                                break;
                            }
                        }
                    }
                    for (MyToken dj : sen_j.tokensList) {
                        all_j += dj.tf_isf;
                        for (MyToken di : sen_i.tokensList) {
                            if (di.equals(dj) || di.isSimilarTo(dj)) {
                                sim_j += dj.tf_isf;
                                break;
                            }
                        }
                    }

                    double simScore = (sim_i / all_i + sim_j / all_j) / 2;
                    if (simScore > SIM_THRESHOLD) {
                        // xóa câu rank thấp hơn; rank bằng nhau thì xóa câu dài hơn
//                        System.out.println(simScore + " và " + i + " và " + j);
                        if (sen_i.getScore() < sen_j.getScore()) {
                            sen_i.isRemove = true;
                        } else if (sen_i.getScore() > sen_j.getScore()) {
                            sen_j.isRemove = true;
                        } else {
                            if (sen_i.tokensList.size() > sen_j.tokensList.size()) { // xóa câu dài hơn
                                sen_i.isRemove = true;
                            } else {
                                sen_j.isRemove = true;
                            }
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
     * Phân giải đồng tham chiếu (???); loại câu trùng lặp, dư thừa; xếp hạng
     * câu.
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
            for (MySentence sentence : sentences) {
                if (sentence.rank == rank && sentence.tokensList.size() > 10) {
                    if (rank + 1 >= sentences.size() || maxWord - counter < sentence.tokensList.size()) {
                        counter = maxWord + 1;
                    } else {
                        sentence.isRemove = false;
                        for (MyToken token : sentence.tokensList) {
                            if (!token.punctuation) {
                                counter += token.word.split("_").length;
                            }
                        }
                    }
                }
            }
            rank++;
        }

        remove();
        return sentences;
    }

    public static void main(String[] args) {
        Synonym.Init();
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/kinhte/KT01.txt");
        MyReducer re = new MyReducer(sentences);
        WordGraphs wg = new WordGraphs(re.reduction());
        MyExtracter se = new MyExtracter(wg.generateSentences());
        sentences = se.extract(100);
        for (MySentence mySentence : sentences) {
            System.out.println(mySentence.toString());
            System.out.println(mySentence.getScore() + " ; " + mySentence.rank);
        }
    }
}
