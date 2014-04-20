/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.sentenceExtraction;

import java.util.ArrayList;
import java.util.List;
import nlp.dict.Punctuation;
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class Sentence {

    public final ArrayList<Datum> dataList;

    public Sentence() {
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
        for (int i = 0; i <= end-start; i++) {
            dataList.remove(start);
        }
    }    

    /**
     * Convert list of data to list of sentences
     *
     * @param data
     * @return
     */
    public static ArrayList<Sentence> DatumToSentence(List<Datum> data) {
        ArrayList<Sentence> sentences = new ArrayList<>();

        Sentence sen = new Sentence();
        for (Datum dt : data) {
            sen.dataList.add(dt);
            if (Punctuation.isEndOfSentence(dt.word)) {
                sentences.add(sen);
                sen = new Sentence();
            }
        }
        return sentences;
    }
    
    public static ArrayList<Datum> SentenceToDatum(ArrayList<Sentence> sentences) {
        ArrayList<Datum> data = new ArrayList<>();
        for (Sentence sentence : sentences) {
            for (Datum datum : sentence.dataList) {
                datum.iSentence = sentences.indexOf(sentence);
                data.add(datum);
            }
        }

        return data;
    }

    public static void main(String[] args) {
        MyTagger ins = new MyTagger();
        String fileNameSource = "corpus/Plaintext/test.txt";
        String strTest = "Bệnh nhân Vũ Dư dùng thuốc Biseptol. "
                + "Sau khi uống, anh ấy có biểu hiện dị ứng với các thành phần của thuốc.";
//        String strTest = "Bố Bách mua thuốc về cho Bách uống. Sau khi uống, anh ấy bị đỏ môi.";
        IOUtil.WriteToFile(fileNameSource, strTest);

        List<Datum> datum = null;
        datum = ins.getData("test");
        ArrayList<Sentence> sens = Sentence.DatumToSentence(datum);
        System.out.println("\n");
        System.out.println(strTest);
        System.out.println("Các chủ ngữ: ");
        for (Sentence sen : sens) {
            int[] indices = sen.getSubject();
            String s = "";
            for (int i = indices[0]; i <= indices[1]; i++) {
                s += sen.dataList.get(i).word + " ";
            }
            System.out.println(s);
        }
    }
}
