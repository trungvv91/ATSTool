/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;

/**
 *
 * @author TRUNG
 */
public class MySentence {

    public final ArrayList<MyToken> tokensList;
    public boolean isRemove;
    public int rank;
    private double score;

    /**
     * Khởi tạo 1 câu rỗng.
     */
    public MySentence() {
        tokensList = new ArrayList<>();
        score = 0;
        rank = 0;
        isRemove = false;
    }

    @Override
    public String toString() {
        String str = "";
        for (MyToken token : tokensList) {
            str += token.word + " ";
        }
        return str + "\n";
    }

    /**
     * kiểm tra câu có trạng ngữ ở đầu không, phân cách bằng dấu phẩy và chủ ngữ
     * sau đó
     *
     * @return vị trí dấu phẩy hoặc -1 nếu không tìm thấy
     */
    private int getAdverbClause() {
        for (int i = 0; i < tokensList.size() - 2; i++) {
            if (tokensList.get(i).word.equals(",") && tokensList.get(i + 1).chunk.endsWith("NP")) {
                return i;
            }
        }
        return -1;
    }

    public int[] getSubject() {
        int start = getAdverbClause() + 1;
        int end = start;
        while (tokensList.get(end).iPhrase == tokensList.get(start).iPhrase) {
            end++;
        }

        int[] indices = {start, end - 1};
        return indices;
    }

    /**
     * Tính điểm tf-isf trung bình trên cả câu.
     *
     * @return
     */
    public double getScore() {
        if (score <= 0) {
            int counter = 0;
            for (MyToken token : tokensList) {
                if (token.tf_isf > 0) {
                    score += token.tf_isf;
                    counter++;
                }
            }
            score /= counter;
        }
        return score;
    }

    /**
     * Cập nhật điểm tf-isf trung bình trên cả câu sau khi reduction.
     *
     */
    public void updateScore() {
        int counter = 0;
        for (MyToken token : tokensList) {
            if (token.tf_isf > 0) {
                score += token.tf_isf;
                counter++;
            }
        }
        score /= counter;        
    }

    /**
     * Xóa cụm token từ start đến hết end
     *
     * @param start
     * @param end
     */
    public void deletePhrase(int start, int end) {
        for (int i = 0; i <= end - start; i++) {
            tokensList.remove(start);
        }
    }

    /**
     * Convert list of data to list of sentences
     *
     * @param data
     * @return
     */
    public static ArrayList<MySentence> DatumToSentence(ArrayList<MyToken> data) {
        ArrayList<MySentence> sentences = new ArrayList<>();

        MySentence sentence = new MySentence();
        for (MyToken dt : data) {
            sentence.tokensList.add(dt);
            if (dt.endOfSentence) {
                sentences.add(sentence);
                sentence = new MySentence();
            }
        }

        return sentences;
    }

    public static ArrayList<MyToken> SentenceToDatum(ArrayList<MySentence> sentences) {
        ArrayList<MyToken> data = new ArrayList<>();
        for (MySentence sentence : sentences) {
            for (int i = 0; i < sentence.tokensList.size(); i++) {
                MyToken datum = sentence.tokensList.get(i);
                datum.iSentence = sentences.indexOf(sentence);
                data.add(datum);
            }
        }

        return data;
    }

    /**
     * Lấy ra K câu có score cao nhất
     *
     * @param sentences
     * @param k
     * @return mảng các chỉ số của K câu
     */
    public static int[] getTopKSentence(ArrayList<MySentence> sentences, int k) {
//        ArrayList<MySentence> sentences = MySentence.DatumToSentence(tokens);
        for (MySentence sen_i : sentences) {
            for (MySentence sen_j : sentences) {
                if (sen_j.getScore() > sen_i.getScore()) {
                    sen_i.rank++;       // bao nhiêu câu có score cao hơn sen_i
                }
            }
        }
        int[] topSenIndex = new int[k];
        int counter = 0;
        int rank = 0;
        while (counter < k) {
            for (int i = 0; i < sentences.size(); i++) {
                if (sentences.get(i).rank == rank) {
                    topSenIndex[counter++] = i;
                    if (counter >= k) {
                        break;
                    }
                }
            }
            rank++;
        }
        return topSenIndex;
    }

    public static void main(String[] args) {
//        MyTokenizer tokenizer = new MyTokenizer();
//        String fileNameSource = "corpus/Plaintext/test.txt";
//        String strTest = "Bệnh nhân Vũ Dư dùng thuốc Biseptol. "
//                + "Sau khi uống, anh ấy có biểu hiện dị ứng với các thành phần của thuốc.";
////        String strTest = "Bố Bách mua thuốc về cho Bách uống. Sau khi uống, anh ấy bị đỏ môi.";
//        IOUtil.WriteToFile(fileNameSource, strTest);
//
//        List<MyToken> tokensList = tokenizer.createTokens("test");
//        ArrayList<MySentence> sens = MySentence.DatumToSentence(tokensList);
//        System.out.println("\n");
//        System.out.println(strTest);
//        System.out.println("Các chủ ngữ: ");
//        for (MySentence sen : sens) {
//            int[] indices = sen.getSubject();
//            String s = "";
//            for (int i = indices[0]; i <= indices[1]; i++) {
//                s += sen.tokensList.get(i).word + " ";
//            }
//            System.out.println(s);
//        }
    }
}
