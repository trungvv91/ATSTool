/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.List;
import nlp.extradata.Punctuation;
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class MySentence {

    public final ArrayList<MyToken> dataList;

    public MySentence() {
        dataList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return dataList.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * kiểm tra câu có trạng ngữ ở đầu không, phân cách bằng dấu phẩy và chủ ngữ
     * sau đó
     *
     * @return vị trí dấu phẩy hoặc -1 nếu không tìm thấy
     */
    private int getAdverbClause() {
        for (int i = 0; i < dataList.size() - 2; i++) {
            if (dataList.get(i).word.equals(",") && dataList.get(i + 1).chunk.endsWith("NP")) {
                return i;
            }
        }
        return -1;
    }

    public int[] getSubject() {
        int start = getAdverbClause() + 1;
        int end = start;
        while (dataList.get(end).iPhrase == dataList.get(start).iPhrase) {
            end++;
        }

        int[] indices = {start, end - 1};
        return indices;
    }

    public ArrayList<String> getNpList() {
        ArrayList<String> npList = new ArrayList<>();

        return npList;
    }

    public void deletePhrase(int start, int end) {
        for (int i = 0; i <= end - start; i++) {
            dataList.remove(start);
        }
    }

    /**
     * Convert list of data to list of sentences
     *
     * @param data
     * @return
     */
    public static ArrayList<MySentence> DatumToSentence(List<MyToken> data) {
        ArrayList<MySentence> sentences = new ArrayList<>();

        MySentence sen = new MySentence();
        for (MyToken dt : data) {
            sen.dataList.add(dt);
            if (Punctuation.isEndOfSentence(dt.word)) {
                sentences.add(sen);
                sen = new MySentence();
            }
        }
        return sentences;
    }

    public static ArrayList<MyToken> SentenceToDatum(ArrayList<MySentence> sentences) {
        ArrayList<MyToken> data = new ArrayList<>();
        for (MySentence sentence : sentences) {
            for (int i = 0; i < sentence.dataList.size(); i++) {
                MyToken datum = sentence.dataList.get(i);
                datum.iSentence = sentences.indexOf(sentence);
                data.add(datum);
            }
        }

        return data;
    }

    public static void main(String[] args) {
//        MyTokenizer tokenizer = new MyTokenizer();
//        String fileNameSource = "corpus/Plaintext/test.txt";
//        String strTest = "Bệnh nhân Vũ Dư dùng thuốc Biseptol. "
//                + "Sau khi uống, anh ấy có biểu hiện dị ứng với các thành phần của thuốc.";
////        String strTest = "Bố Bách mua thuốc về cho Bách uống. Sau khi uống, anh ấy bị đỏ môi.";
//        IOUtil.WriteToFile(fileNameSource, strTest);
//
//        List<MyToken> tokens = tokenizer.createTokens("test");
//        ArrayList<MySentence> sens = MySentence.DatumToSentence(tokens);
//        System.out.println("\n");
//        System.out.println(strTest);
//        System.out.println("Các chủ ngữ: ");
//        for (MySentence sen : sens) {
//            int[] indices = sen.getSubject();
//            String s = "";
//            for (int i = indices[0]; i <= indices[1]; i++) {
//                s += sen.dataList.get(i).word + " ";
//            }
//            System.out.println(s);
//        }
    }
}
