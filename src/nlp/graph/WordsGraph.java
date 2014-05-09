/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.graph;

import nlp.textprocess.MyToken;
import java.util.ArrayList;
import nlp.textprocess.MySentence;
import nlp.textprocess.MyExtracter;
import nlp.textprocess.MyReduction;
import nlp.textprocess.MyTokenizer;
import nlp.util.MyStringUtil;

/**
 *
 * @author Trung
 */
public class WordsGraph {

    public static String graphing(String filename, int maxWord) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MyToken> tokens = tokenizer.createTokens(filename);
        MyExtracter se = new MyExtracter(tokens);
        MyReduction re = new MyReduction(se.extract());
        ArrayList<MySentence> sentences = re.reduction();

        // <editor-fold defaultstate="collapsed" desc="xét 2 câu liên tiếp --> trùng subject hoặc verb thì ghép">
/*
         List<Datum> tmpList = new ArrayList<>();
         for (int i = 0; i < sens.size() - 2; i++) {
         List<Datum> seni = sens.get(i);
         int j = i + 1;
         int indexToAdd = -1;
         List<Datum> senj = sens.get(j);
         tmpList.clear();
         for (int k = 0; k < seni.size(); k++) {
         MyToken dik = seni.get(k);
         for (int h = 0; h < senj.size(); h++) {
         MyToken djh = senj.get(h);
         /// co-ref là N or Np ???
         if (dik.equals(djh) && (dik.posTag.equals("N") || dik.posTag.equals("Np"))) {
         String phrasei = "";
         int endChunki = k;
         /// tim iPhrase cua di va dj
         if (k > 0 && seni.get(k - 1).iPhrase == dik.iPhrase) {
         phrasei += seni.get(k - 1).word.toLowerCase() + " ";
         }
         phrasei += dik.word.toLowerCase();
         if (k < seni.size() - 1 && seni.get(k + 1).iPhrase == dik.iPhrase) {
         phrasei += seni.get(k + 1).word.toLowerCase();
         endChunki = k + 1;
         }
         String phrasej = "";
         int endChunkj = h;
         if (h > 0 && senj.get(h - 1).iPhrase == djh.iPhrase) {
         phrasej += senj.get(h - 1).word + " ";
         }
         phrasej += djh.word.toLowerCase();
         if (k < senj.size() - 1 && senj.get(h + 1).iPhrase == djh.iPhrase) {
         phrasej += senj.get(h + 1).word.toLowerCase();
         endChunkj = h + 1;
         }
         /// Bệnh_nhân Vũ_Dư vs. Vũ_Dư
         if ((phrasej.contains(phrasei) || phrasei.contains(phrasej))
         && endChunki < seni.size() - 1
         && (seni.get(endChunki + 1).posTag.equals("R")
         || seni.get(endChunki + 1).posTag.equals("V"))) {
         boolean hasImportantWord = false;
         for (int l = 0; l < k; l++) {
         if (seni.get(l).keyword == true) {
         hasImportantWord = true;    /// trước dik có 1 từ important
         break;
         }
         }
         if (hasImportantWord == false) {
         indexToAdd = endChunkj + 1;
         for (int l = endChunki + 1; l < seni.size() - 1; l++) {
         tmpList.add(seni.get(l));   /// phần còn lại của câu seni
         }
         }
         }
         }
         }
         }       // end for k
         /// ghép seni và senj
         if (!tmpList.isEmpty() && indexToAdd > -1) {
         senj.addAll(indexToAdd, tmpList);
         senj.add(indexToAdd + tmpList.size(), new MyToken("và", "C"));
         sens.remove(i);
         i--;
         }
         }
         for (int i = 0; i < sens.size() - 2; i++) {
         //ghep 2 cau neu trung Verb
         List<Datum> seni = sens.get(i);
         List<Datum> senj = sens.get(i + 1);
         /// tìm subject
         String verbi = "";
         for (MyToken di : seni) {
         if (di.posTag.equals("V")) {
         for (int k = seni.indexOf(di); seni.get(k).iPhrase == di.iPhrase; k++) {
         verbi += seni.get(k).word + " ";
         }
         break;
         }
         }
         String verbj = "";
         for (MyToken dj : senj) {
         if (dj.posTag.equals("V")) {
         for (int k = senj.indexOf(dj); senj.get(k).iPhrase == dj.iPhrase; k++) {
         verbj += senj.get(k).word + " ";
         }
         break;
         }
         }
         if (verbi.equals(verbj)) {
         Random r = new Random();
         String[] list = {"và", ","};
         MyToken dt = new MyToken(list[r.nextInt(2)], "C");
         dt.chunk = "C";
         dt.stopWord = true;
         int k;
         for (k = 0; seni.get(k).iPhrase == 0; k++) {
         }
         seni.add(k, dt);
         for (int l = 0; senj.get(l).iPhrase == 0; l++) {
         seni.add(k + 1, senj.get(l));
         }
         sens.remove(i + 1);     // remove senj
         }
         }   /// end for
         */
        // </editor-fold>
        /// lọc WordMax, Set senExclude lưu chỉ số những câu bị loại
        /// bỏ các câu tf_idf thấp nhất, cho đến khi word < wordMax
        //
//        String outputFilename = "temp/autosum.txt";
//        IOUtil.WriteToFile(outputFilename, outString);
        String outString = "";
        int nWord = 0;
        for (MySentence sentence : sentences) {
            outString += " " + MyStringUtil.capitalize(sentence.tokensList.get(0).word);
            for (int i = 1; i < sentence.tokensList.size(); i++) {
                MyToken token = sentence.tokensList.get(i);
                if (token.punctuation) {
                    outString += token.word;
                } else {
                    outString += " " + token.word;
                    nWord++;
                }
            }
//            outString += "\n";
        }
        outString = outString.trim();
//        System.out.println(outString);
        System.out.println(outString.replaceAll("_", " "));
        System.out.println("Số từ: " + nWord);
        return outString;
    }

    public static void main(String[] args) {
        WordsGraph.graphing("corpus/Plaintext/1.txt", 120);
    }
}
