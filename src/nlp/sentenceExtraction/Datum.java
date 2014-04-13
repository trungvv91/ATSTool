/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.sentenceExtraction;

import java.util.Objects;

/**
 *
 * @author Manh Tien
 */
public class Datum {

    /**
     * the position of sentence containing Datum
     */
    public int iSentence;
    /**
     * the position of phrase in sentence containing Datum
     */
    public int iPhrase;
    public String word;
    public String posTag;
    public String chunk;
    /**
     * tf-idf score
     */
    public int tf;
    public double idf;
    public double score;
    public boolean stopWord = false;
    public boolean semiStopWord = false;
    public boolean importance = false;

    public Datum(String word, String posTag) {
        this.word = word;
        this.posTag = posTag;
        tf = 0;
        idf = 0;
        iPhrase = -1;
    }

    @Override
    public String toString() {
        return word + " " + posTag + " " + chunk + " " + iPhrase + " " + iSentence + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        boolean rs;
        if (obj == null || this.getClass() != obj.getClass()) {
            rs = false;
        } else {
            final Datum objDatum = (Datum) obj;
            rs = this.word.toLowerCase().equals(objDatum.word.toLowerCase()) && this.posTag.equals(objDatum.posTag);
        }
        return rs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.word.toLowerCase());
        hash = 83 * hash + Objects.hashCode(this.posTag);
        return hash;
    }

    public static void main(String[] args) {
        Datum d1 = new Datum("hello", "V");
        Datum d2 = new Datum("Hello", "V");
        System.out.println(d1.equals(d2));
    }
}
