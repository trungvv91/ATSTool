/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nlp.extradata.Punctuation;
import nlp.extradata.Synonym;

/**
 *
 * @author Trung
 */
public class MyToken {

    public String word;
    public String posTag;
    public String chunk;

    /**
     * the position of sentence
     */
    public int nSentence;
    /**
     * the position of phrase in sentence
     */
    public int nPhrase;
    /**
     * the position of token in sentence
     */
    public int nPosition;

    /**
     * tf-idf và tf-isf
     */
    public double tf_idf;
    public double tf_isf;

    public boolean stopWord;
    public boolean semiStopWord;
    public boolean punctuation;
    public boolean endOfSentence;
    public boolean keyword;

    public MyToken(String word, String posTag, String chunk) {
        this.word = word;
        this.posTag = posTag;
        this.chunk = chunk;
        nPhrase = -1;
        stopWord = false;
        semiStopWord = false;
        punctuation = false;
        endOfSentence = false;
        keyword = false;
    }

    public static int getNumberOfSentences(ArrayList<MyToken> data) {
        return data.get(data.size() - 1).nSentence + 1;
    }

    public static List<MyToken> SentenceToDatum(List<ArrayList<MyToken>> sentences) {
        ArrayList<MyToken> data = new ArrayList<>();
        for (List<MyToken> sentence : sentences) {
            for (int i = 0; i < sentence.size(); i++) {
                MyToken datum = sentence.get(i);
                datum.nSentence = i;
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
        return word + "\t" + posTag + "\t" + chunk + "\t" + nPosition + "\t" + nPhrase + "\t" + nSentence
                + "\t" + ((int) (tf_isf * 1000)) / 1000.0 + "\t" + ((stopWord || semiStopWord) ? "1" : "0")
                + "\t" + (keyword ? "1" : "0");
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

    public boolean isSimilarTo(Object obj) {
        boolean rs = false;
        if (obj == null || this.getClass() != obj.getClass()) {
            rs = false;
        } else {
            final MyToken objDatum = (MyToken) obj;
            String[] synonyms = Synonym.getSynonyms(word);
            for (String synonym : synonyms) {
                if (synonym.equals(objDatum.word)) {
                    rs = true;
                    break;
                }
            }
            rs = rs && this.posTag.equals(objDatum.posTag);
        }
        return rs;
    }

    public static void main(String[] args) {
        MyToken d1 = new MyToken("hello", "V", "B-Np");
        MyToken d2 = new MyToken("Hello", "V", "B-Np");
        System.out.println(d1.equals(d2));
//        System.out.println("Ngày 27-7 vừa qua, ngày hội hiến máu \"Giọt hồng tri ân\" lần thứ 3 năm 2013 đã được tổ chức tại sân vận động quốc gia Mỹ Đình, theo đó ngày hội này thu được khoảng 3.000 đơn vị máu bổ sung vào ngân hàng máu để cứu giúp các bệnh nhân.Với thông điệp \"Mỗi trái tim - một ngọn lửa anh hùng\", ngày hội tiếp tục hướng tới mục tiêu vận động giới trẻ và cộng đồng tham gia hiến máu tình nguyện nhằm khắc phục tình trạng khan hiếm nguồn người hiến máu dịp Hè.Trong ngày hội, bên cạnh chương trình trọng tâm hiến máu tình nguyện, còn có nhiều hoạt động thể hiện sự tri ân."
//                .matches("(.*?)\\.[^\\s](.*)"));
    }
}
