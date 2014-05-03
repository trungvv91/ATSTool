/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class Punctuation {

    public final static String PUNCTUATIONS[] = {"...", ".", ",", "!", "?", ";", "\"", ":", "-", "'", "(", ")"};

    public static boolean isPuctuation(String s) {
        for (String punc : PUNCTUATIONS) {
            if (s.equals(punc)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEndOfSentence(String s) {
        return (s.equals(".") || s.equals("?") || s.equals("!"));
    }
}
