/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.graph;

import nlp.textprocess.MyToken;
import java.util.ArrayList;
import java.util.HashSet;
import nlp.extradata.Conjunction;
import nlp.textprocess.MySentence;
import nlp.textprocess.MyExtracter;
import nlp.textprocess.MyTokenizer;
import nlp.util.IOUtil;
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
        ArrayList<MySentence> sentences = se.extract();

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
         if (seni.get(l).importance == true) {
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
        //        
        // <editor-fold defaultstate="collapsed" desc="find essential fragments">
        System.out.println("----------");
        ArrayList<Integer[]> termIndex = new ArrayList<>();     // lưu start & end of unsplitable phrases
        for (MySentence sentence : sentences) {
            ArrayList<MyToken> sen = sentence.tokensList;
            Integer[] index = new Integer[2];
            int i, j;
            for (i = 0; i < sen.size(); i++) {
                if (sen.get(i).importance) {
                    break;              // tìm first important token
                }
            }
            index[0] = (i == sen.size()) ? -1 : i;
            for (j = sen.size() - 1; j > -1; j--) {
                if (sen.get(j).importance) {
                    break;              // tìm last important token
                }
            }
            index[1] = j;

            i = index[0];
            while (i > 0 && sen.get(i - 1).iPhrase == sen.get(index[0]).iPhrase) {
                i--;
            }
            index[0] = i;

            while (j > -1 && j < sen.size() - 1 && sen.get(j + 1).iPhrase == sen.get(index[1]).iPhrase) {
                j++;
            }
            index[1] = j;

            termIndex.add(index);
        }
        System.out.println("----------");
        // </editor-fold>

        /// Bắt đầu xử lý
        for (MySentence sentence : sentences) {
            ArrayList<MyToken> sen = sentence.tokensList;
            int senIndex = sentences.indexOf(sentence);
            if (termIndex.get(senIndex)[0] != -1 && termIndex.get(senIndex)[1] != -1) {     /// !!! null --> -1
                int sIndex = termIndex.get(senIndex)[0];
                int eIndex = termIndex.get(senIndex)[1];

                /// Kiểm tra bắt đầu là 1 VP thì nối NP trước đó vào
                if (sIndex > 0 && sen.get(sIndex).chunk.equals("B-VP")) {
                    for (int i = sIndex - 1; i >= 0; i--) {
                        MyToken d = sen.get(i);
                        if (d.chunk.equals("B-NP")) {
                            sIndex = i;
                            break;
                        }
                    }
                }

                /// Kiểm tra kết thúc là 1 NP thì nối V và A sau đó vào
                if (eIndex < sen.size() - 1 && (sen.get(eIndex + 1).posTag.equals("V") || sen.get(eIndex + 1).posTag.equals("A"))
                        && ((sen.get(eIndex).chunk.equals("B-NP") || sen.get(eIndex).chunk.equals("I-NP")))) {
                    int tIndex = eIndex + 1;
                    int phrase = sen.get(tIndex).iPhrase;
                    while (tIndex < sen.size() - 1 && sen.get(tIndex + 1).iPhrase == phrase) {
                        tIndex++;
                    }
                    eIndex = tIndex;
                }

                /// Kiểm tra kết thúc là 1 V thì nối cụm VP vào
                if (eIndex < sen.size() - 1 && sen.get(eIndex).posTag.equals("V")
                        && sen.get(eIndex + 1).chunk.equals("B-NP")) {
                    int tIndex = eIndex + 1;
                    int phrase = sen.get(tIndex).iPhrase;
                    while (tIndex < sen.size() - 1 && sen.get(tIndex + 1).iPhrase == phrase) {
                        tIndex++;
                    }
                    eIndex = tIndex;
                    if (sen.get(eIndex + 1).chunk.equals("B-VP") && eIndex < sen.size() - 1) {
                        int tIndex1 = eIndex + 1;
                        int phrase1 = sen.get(tIndex1).iPhrase;
                        while (tIndex1 < sen.size() - 1 && sen.get(tIndex1 + 1).iPhrase == phrase1) {
                            tIndex1++;
                        }
                        eIndex = tIndex1;
                    }
                }

                boolean hasV = false;
                boolean hasN = false;
                for (int i = sIndex; i <= eIndex; i++) {
                    MyToken d = sen.get(i);
                    if (d != null) {
                        if (d.posTag.equals("V")) {
                            hasV = true;
                        } else if (d.chunk.equals("B-NP")) {
                            hasN = true;
                        }
                    }
                }

                /// Có NP nhưng ko có VP thì tìm VP ở trước đó, rồi tìm tiếp NP trước đó
                if (sIndex > 0 && hasV == false) {
                    if (hasN) {                     /// Có NP nhưng ko có VP
                        for (int i = sIndex - 1; i >= 0; i--) {
                            if (sen.get(i).posTag.equals("V")) {
                                sIndex = i;
                                if (i > 0) {
                                    for (int j = i - 1; j >= 0; j--) {
                                        if (sen.get(j).chunk.equals("B-NP")) {
                                            sIndex = j;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } else {                        /// Không có cả NP và VP
                        boolean foundV = false;
                        for (int i = sIndex - 1; i >= 0; i--) {
                            if (sen.get(i).chunk.equals("B-VP")) {
                                sIndex = i;
                                foundV = true;
                                if (i > 0) {
                                    for (int j = i - 1; j >= 0; j--) {
                                        if (sen.get(j).chunk.equals("B-NP")) {
                                            sIndex = j;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        if (!foundV) {              /// nếu ko tìm thấy VP ở trước thì lấy luôn NP
                            for (int i = sIndex - 1; i >= 0; i--) {
                                if (sen.get(i).chunk.equals("B-NP")) {
                                    sIndex = i;
                                    break;
                                }
                            }
                        }
                    }
                }

                /// Kiểm tra DECLARE_WORDS word
                boolean hasDeclare = false;
                int iDec = -1;
                for (int i = sen.size() - 1; i > -1; i--) {
                    if (Conjunction.checkDeclareWord(sen.get(i).word)) {
                        hasDeclare = true;
                        iDec = i;
                        break;
                    }
                }
                if (hasDeclare) {
                    sIndex = (iDec < sIndex) ? iDec + 1 : 0;        /// ???
                }

                /// Kiểm tra Conjunction
                boolean hasFConj = false;
                int iConj = -1;
                int jConj;
                int indexOfConj = -1; //Thu tu conjunction trong mang
                for (MyToken di : sen) {
                    indexOfConj = Conjunction.checkConjunction(di.word);
                    if (indexOfConj > -1) {
                        hasFConj = true;
                        iConj = sen.indexOf(di);
                        break;
                    }
                }

                if (hasFConj) {
                    if (Conjunction.CONJUNCTIONS[indexOfConj][1] != null) {     /// cặp liên từ --> tìm liên từ thứ 2
                        for (int i = iConj + 1; i < sen.size(); i++) {
                            if (sen.get(i).word.toLowerCase().equals(Conjunction.CONJUNCTIONS[indexOfConj][1])) {
                                jConj = i;
                                boolean hasIptWord = false;     // kiem tra important word giua 2 conjunction
                                for (int j = iConj; j <= jConj; j++) {
                                    if (sen.get(j).importance == true) {
                                        hasIptWord = true;
                                        break;
                                    }
                                }
                                if (hasIptWord) {
                                    sIndex = 0;     /// ???
                                } else {
                                    if ("A".equals(Conjunction.CONJUNCTIONS[indexOfConj][2])) {
                                        sIndex = jConj + 1;
                                    } else {        /// A thì bỏ phần trước Conj, B thì bỏ phần sau Conj
                                        eIndex = jConj - 1;
                                    }
                                }
                            }
                            break;
                        }
                    } else {        /// chỉ có 1 liên từ
                        boolean hasIptWord = false;
                        for (int i = 0; i <= iConj; i++) {
                            MyToken di = sen.get(i);
                            if (di.importance == true) {
                                hasIptWord = true;
                                break;
                            }
                        }
                        if (hasIptWord) {
                            sIndex = 0;
                        } else {
                            if ("A".equals(Conjunction.CONJUNCTIONS[indexOfConj][2])) {
                                sIndex = iConj + 1;
                            } else {
                                eIndex = iConj - 1;
                            }
                        }
                    }
                }

                if (eIndex > -1 && ("và".equals(sen.get(eIndex).word) || "hoặc".equals(sen.get(eIndex).word)
                        || "và ".equals(sen.get(eIndex).word) || "hoặc ".equals(sen.get(eIndex).word))) {
                    eIndex--;
                }
                if (sIndex > -1 && (sen.get(sIndex).word.equals(",") || sen.get(sIndex).word.equals(", "))) {
                    sIndex++;
                }

                termIndex.get(senIndex)[0] = sIndex;
                termIndex.get(senIndex)[1] = eIndex;
            }
        }   // end of for sens

        /// lọc WordMax, Set senExclude lưu chỉ số những câu bị loại
        /// bỏ các câu tf_idf thấp nhất, cho đến khi word < wordMax
        HashSet<Integer> senExclude = new HashSet<>();
        int count = 0;      /// số câu bị loại
        int words = maxWord + 1;
        while (words > maxWord) {
            words = 0;
            for (MySentence sentence : sentences) {
                ArrayList<MyToken> sen = sentence.tokensList;
                if (!senExclude.contains(sentences.indexOf(sentence))) {
                    for (MyToken dt : sen) {
                        if (!dt.chunk.equals("O")) {
                            words++;
                        }
                    }
                }
            }
            if (words > maxWord) {
                count++;
                /// lấy thằng thấp nhất
///???                senExclude.add(se.mapSenOrderByScore.get(sentences.size() - count));
            }
        }

        /// Done, decoration
//        System.out.println("Decoration");
        int numOfWords = 0;
        String outString = "";
        for (MySentence sentence : sentences) {
            ArrayList<MyToken> sen = sentence.tokensList;
            // Capitalize the first word in a sentence
            if (!senExclude.contains(sentences.indexOf(sentence))) {
                int senIndex = sentences.indexOf(sentence);
                int sIndex = termIndex.get(senIndex)[0];
                int eIndex = termIndex.get(senIndex)[1];
                if (sIndex != -1 && eIndex != -1) {
                    sen.get(sIndex).word = MyStringUtil.capitalize(sen.get(sIndex).word);
                    for (MyToken d : sen) {
                        if (!d.chunk.endsWith("O")) {
                            numOfWords++;
                        }
                        if (sen.indexOf(d) >= sIndex && sen.indexOf(d) <= eIndex) {
                            /// thuộc essential fragment
                            System.out.print(d.word + " ");
                            outString += d.word.replace("_", " ");
                            outString += " ";
                        }
                    }
                    System.out.println("");
                    outString += "\n";
                }
            }
        }
        System.out.println("Number of words: " + numOfWords);

//        String outputFilename = "temp/autosum.txt";
//        IOUtil.WriteToFile(outputFilename, outString);
        return outString;
    }

    public static void main(String[] args) {
        WordsGraph.graphing("corpus/Plaintext/1.txt", 120);
    }
}
