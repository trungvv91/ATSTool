/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nlp.extradata.Punctuation;

/**
 *
 * @author Trung
 */
public class MyToken {

    public String word;
    public String posTag;
    public String chunk;

    /**
     * the position of sentence containing MyToken
     */
    public int iSentence;
    /**
     * the position of phrase in sentence containing MyToken
     */
    public int iPhrase;
    /**
     * the position of phrase in sentence containing MyToken
     */
    public int iPosition;

    /**
     * tf-idf và tf-isf
     */
    public int tf;
    public double idf;
    public double tf_idf;
    public double tf_isf;

    public boolean stopWord;
    public boolean semiStopWord;
    public boolean punctuation;
    public boolean endOfSentence;
    public boolean importance;

    public MyToken(String word, String posTag, String chunk) {
        this.word = word;
        this.posTag = posTag;
        this.chunk = chunk;
        tf = 0;
        idf = 0;
        iPhrase = -1;
        stopWord = false;
        semiStopWord = false;
        punctuation = false;
        endOfSentence = false;
        importance = false;
    }

    public static int getNumberOfSentences(List<MyToken> data) {
        return data.get(data.size() - 1).iSentence + 1;
    }

    public static List<MyToken> SentenceToDatum(List<ArrayList<MyToken>> sentences) {
        ArrayList<MyToken> data = new ArrayList<>();
        for (List<MyToken> sentence : sentences) {
            for (int i = 0; i < sentence.size(); i++) {
                MyToken datum = sentence.get(i);
                datum.iSentence = i;
                data.add(datum);
            }
        }

        return data;
    }

    public static ArrayList<String> DatumToWord(List<MyToken> data) {
        ArrayList<String> wordList = new ArrayList<>();
        for (MyToken dt : data) {
            wordList.add(dt.word);
        }
        return wordList;
    }

    public static ArrayList<ArrayList<MyToken>> DatumToSentence(List<MyToken> data) {
        ArrayList<ArrayList<MyToken>> sentenceArray = new ArrayList<>();

        ArrayList<MyToken> senList = new ArrayList<>();
        for (MyToken dt : data) {
            senList.add(dt);
            if (Punctuation.isEndOfSentence(dt.word)) {
                sentenceArray.add(senList);
                senList = new ArrayList<>();
            } else {
            }
        }
        return sentenceArray;
    }

    @Override
    public String toString() {
//        return word + "\t" + posTag + "\t" + chunk + "\t(" + iPosition + "," + iPhrase + "," + iSentence + ")\t"
//                + ((int) (tf_isf * 1000)) / 1000.0 + "\t" + ((stopWord || semiStopWord) ? "1" : "0");
        return word + "\t" + posTag + "\t" + chunk + "\t" + iPosition + "\t" + iPhrase + "\t" + iSentence
                + "\t" + ((int) (tf_isf * 1000)) / 1000.0 + "\t" + ((stopWord || semiStopWord) ? "1" : "0");
    }

    @Override
    public boolean equals(Object obj) {
        boolean rs;
        if (obj == null || this.getClass() != obj.getClass()) {
            rs = false;
        } else {
            final MyToken objDatum = (MyToken) obj;
            rs = this.word.equals(objDatum.word) && this.posTag.equals(objDatum.posTag);
        }
        return rs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.word);
        hash = 83 * hash + Objects.hashCode(this.posTag);
        return hash;
    }

    public MyToken copy() {
        MyToken newDatum = new MyToken(word, posTag, chunk);
        newDatum.iSentence = this.iSentence;
        newDatum.iPhrase = this.iPhrase;
        newDatum.iPosition = this.iPosition;
        newDatum.tf = this.tf;
        newDatum.idf = this.idf;
        newDatum.tf_isf = this.tf_isf;
        return newDatum;
    }

    public static void main(String[] args) {
        MyToken d1 = new MyToken("hello", "V", "B-Np");
        MyToken d2 = new MyToken("Hello", "V", "B-Np");
        System.out.println(d1.equals(d2));
//        System.out.println("Ngày 27-7 vừa qua, ngày hội hiến máu \"Giọt hồng tri ân\" lần thứ 3 năm 2013 đã được tổ chức tại sân vận động quốc gia Mỹ Đình, theo đó ngày hội này thu được khoảng 3.000 đơn vị máu bổ sung vào ngân hàng máu để cứu giúp các bệnh nhân.Với thông điệp \"Mỗi trái tim - một ngọn lửa anh hùng\", ngày hội tiếp tục hướng tới mục tiêu vận động giới trẻ và cộng đồng tham gia hiến máu tình nguyện nhằm khắc phục tình trạng khan hiếm nguồn người hiến máu dịp Hè.Trong ngày hội, bên cạnh chương trình trọng tâm hiến máu tình nguyện, còn có nhiều hoạt động thể hiện sự tri ân."
//                .matches("(.*?)\\.[^\\s](.*)"));
    }
}
