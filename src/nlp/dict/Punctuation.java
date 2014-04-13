/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.dict;

/**
 *
 * @author TRUNG
 */
public class Punctuation {
    
    public final static String PUNCTUATIONS[] = {"...", ".", ",", "!", "?", ";", "\"", ":", "-", "'"};

    public static boolean isPuctuation(String s) {
        for (String PUNCTUATIONS1 : PUNCTUATIONS) {
            if (s.equals(PUNCTUATIONS1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEndOfSentence(String s) {
        return (s.equals(".") || s.equals("?") || s.equals("!") || s.equals(";"));
    }
}
