/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.decompose;

import nlp.sentenceExtraction.Datum;

/**
 *
 * @author TRUNG
 */
public class TrainData {

    public static enum yVALUE {

        NONE, REMOVE, RETAIN, REPLACE
    };

    private final Datum datum;
    private final yVALUE y;

    public TrainData(Datum datum, yVALUE y) {
        this.datum = datum;
        this.y = y;
    }

    public Datum getDatum() {
        return datum;
    }

    public yVALUE getY() {
        return y;
    }

}
