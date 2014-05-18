/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.decompose;

import nlp.textprocess.MyToken;

/**
 *
 * @author TRUNG
 */
public class TrainData {

    public static enum yVALUE {

        NONE, REMOVE, RETAIN, REPLACE
    };

    public String word;
    public String posTag;
    public String chunk;
    public double iSentence;
//    public double iPhrase;
    public double iPosition;
    public double tf_isf;
    public double tf_idf;
    public int punctuation;
    public boolean stopWord;
    public boolean semiStopWord;
    public boolean isInTopKSens;
    public boolean isInCuePhrase;
    public boolean endOfSentence;
    public yVALUE y;

    public TrainData(MyToken datum) {
        this.word = datum.word;
        this.posTag = datum.posTag;
        this.chunk = datum.chunk;
        this.iSentence = datum.iSentence;
//        this.iPhrase = datum.iPhrase;
        this.iPosition = datum.iPosition;
        if (datum.punctuation) {
            if (datum.endOfSentence) {
                this.punctuation = 2;
            } else {
                this.punctuation = 1;
            }
        } else {
            this.punctuation = 0;
        }
        this.stopWord = datum.stopWord;
        this.semiStopWord = datum.semiStopWord;
        this.endOfSentence = datum.endOfSentence;
        this.tf_isf = datum.tf_isf;
        this.tf_idf = datum.tf_idf;
        this.isInTopKSens = false;
        this.isInCuePhrase = false;
        this.y = yVALUE.NONE;
    }

    public TrainData(MyToken datum, yVALUE y) {
        this(datum);
        this.y = y;
    }

    @Override
    public String toString() {
//        double _iSentence = ((int) (iSentence * 10)) / 10.0;
//        double _iPosition = ((int) (iPosition * 10)) / 10.0;
//        double _tf_isf = ((int) (tf_isf * 100)) / 100.0;
        int _iSentence = (int) (iSentence * 10);
        int _iPosition = (int) (iPosition * 10);
        int _tf_isf = (int) (tf_isf * 100);
        int _tf_idf = (int) (tf_idf);
        return word + "\t" + posTag + "\t" + chunk + "\t" + _iSentence + "\t" + _iPosition + "\t"
                + _tf_isf + "\t" + _tf_idf + "\t" + ((stopWord) ? "1" : "0") + "\t"
                + (isInTopKSens ? "1" : "0") + "\t" + (isInCuePhrase ? "1" : "0") + "\t"
                + punctuation + ((y == yVALUE.NONE) ? "" : ("\t" + y));
    }

    public static void main(String[] args) {
        System.out.println("REMOVE".equals(yVALUE.REMOVE.toString()));
    }
}
