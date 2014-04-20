/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.sentenceExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nlp.dict.Punctuation;

/**
 *
 * @author Trung
 */
public class Datum {

    public String word;
    public String posTag;
    public String chunk;

    /**
     * the position of sentence containing Datum
     */
    public int iSentence;
    /**
     * the position of phrase in sentence containing Datum
     */
    public int iPhrase;
    /**
     * the position of phrase in sentence containing Datum
     */
    public int iPosition;    

    /**
     * tf-idf và tf-isf
     */
    public int tf;
    public double idf;
//    public double tf_idf;
    public double tf_isf;

    public boolean stopWord = false;
    public boolean semiStopWord = false;
    public boolean importance = false;

    public Datum(String word, String posTag, String chunk) {
        this.word = word;
        this.posTag = posTag;
        this.chunk = chunk;
        tf = 0;
        idf = 0;
        iPhrase = -1;
    }

    @Override
    public String toString() {
        return word + "\t" + posTag + "\t" + chunk + "\t(" + iPhrase + "," + iSentence + ")\t"
                + ((int) (tf_isf * 1000)) / 1000.0 + "\t" + (stopWord || semiStopWord);
    }

    @Override
    public boolean equals(Object obj) {
        boolean rs;
        if (obj == null || this.getClass() != obj.getClass()) {
            rs = false;
        } else {
            final Datum objDatum = (Datum) obj;
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
    
    public static int getNumberOfSentences(List<Datum> data) {
        return data.get(data.size() - 1).iSentence + 1;
    }

    public static List<Datum> SentenceToDatum(List<ArrayList<Datum>> sentences) {
        ArrayList<Datum> data = new ArrayList<>();
        for (List<Datum> sentence : sentences) {
            for (Datum datum : sentence) {
                datum.iSentence = sentences.indexOf(sentence);
                data.add(datum);
            }
        }

        return data;
    }

    public static ArrayList<String> DatumToWord(List<Datum> data) {
        ArrayList<String> wordList = new ArrayList<>();
        for (Datum dt : data) {
            wordList.add(dt.word);
        }
        return wordList;
    }

    public static ArrayList<ArrayList<Datum>> DatumToSentence(List<Datum> data) {
        ArrayList<ArrayList<Datum>> sentenceArray = new ArrayList<>();

        ArrayList<Datum> senList = new ArrayList<>();
        for (Datum dt : data) {
            senList.add(dt);
            if (Punctuation.isEndOfSentence(dt.word)) {
                sentenceArray.add(senList);
                senList = new ArrayList<>();
            } else {
            }
        }
        return sentenceArray;
    }

    public static void main(String[] args) {
        Datum d1 = new Datum("hello", "V", "B-Np");
        Datum d2 = new Datum("Hello", "V", "B-Np");
        System.out.println(d1.equals(d2));
    }
}
