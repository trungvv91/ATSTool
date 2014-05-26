/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

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

    public static void main(String[] args) {
//        for (int i = 5; i < 20; i++) {
//            System.out.println(i * 0.8571428571428571);
//        }
        double r = 2.0 / 10;
        double p = 2.0 / 5;
        System.out.println((2 * r * p) / (r + p));
    }
}
