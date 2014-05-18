/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;
import java.util.TreeSet;
import nlp.decompose.Decomposer;
import nlp.decompose.TrainData;
import nlp.extradata.Conjunction;
import nlp.util.CmdUtil;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

/**
 *
 * @author TRUNG
 */
public class MyReducer {

    private final CmdUtil cmd;
    private final ArrayList<MySentence> sentences;
    private final int[][] fragments;

    public MyReducer(ArrayList<MySentence> sentences) {
        System.out.println("Begin reduction.......");
        this.sentences = sentences;
        cmd = new CmdUtil();
        fragments = new int[sentences.size()][2];
    }

    private void crfReduction() {
        // setup các từ trong top K câu
        int[] topKSentence = MySentence.getTopKSentence(sentences, (int) (sentences.size() * Decomposer.TOP_K_SENTENCES));
        TreeSet<String> wordInTopKSentence = new TreeSet<>();
        for (MySentence sentence : sentences) {
            for (MyToken token : sentence.tokensList) {
                for (int j = 0; j < topKSentence.length; j++) {
                    if (token.word.length() > 3 && !token.semiStopWord
                            && !token.stopWord && topKSentence[j] == token.iSentence) {
                        wordInTopKSentence.add(token.word);
                    }
                }
            }
        }

        // setup train data list
        ArrayList<TrainData> trainList = new ArrayList<>();
        for (MySentence sentence : sentences) {
            for (MyToken token : sentence.tokensList) {
                TrainData trainData = new TrainData(token);
                trainData.isInTopKSens = wordInTopKSentence.contains(trainData.word);
                trainList.add(trainData);
            }
        }

        // căn lại vị trí tương đối
        double nSentences = sentences.size();
        double nWords;
        for (TrainData trainData : trainList) {
            nWords = sentences.get((int) trainData.iSentence).tokensList.size();
            trainData.iPosition /= nWords;
            trainData.iSentence /= nSentences;
        }

        // xâu data để ghi ra file test
        String str = trainList.get(0).toString() + "\n";
        for (int j = 1; j < trainList.size(); j++) {
            TrainData trainData = trainList.get(j);
            if (trainData.iSentence != trainList.get(j - 1).iSentence) {
                str += "\n";        // các câu cách nhau bởi dòng trống
            }
            str += trainData.toString() + "\n";
        }
        String testFile = "temp/reduct.test";
        IOUtil.WriteToFile(testFile, str + "\n");

        // chạy crfReduction trên crf++
        String resultFile = "temp/reduct.result";
        cmd.runCmd(cmd.crf_test(CmdUtil.REDUCTION_MODEL, testFile, resultFile));

        // remove tokens
        ArrayList<String> lines = IOUtil.ReadFileByLine(resultFile, true);
        for (int l = 0, s = 0, t = 0; l < lines.size(); l++) {
            if (lines.get(l).isEmpty()) {
                s++;
                t = 0;
            } else {
                MySentence sentence = sentences.get(s);
                String[] features = lines.get(l).split("\\s");
                if (features[0].equals(sentence.tokensList.get(t).word)) {
                    if (features[features.length - 1].equals(TrainData.yVALUE.REMOVE.toString())) {
                        sentence.tokensList.remove(t);
                    } else {
                        t++;
                    }
                }
            }
        }
    }

    /**
     * Tìm essential fragments (đảm bảo phrase) sau đó complete the end of
     * sentence
     */
    private void completingTheEnd() {
        for (int si = 0; si < sentences.size(); si++) {
            ArrayList<MyToken> tokens = sentences.get(si).tokensList;
            fragments[si][0] = 0;     // với begin thì chọn luôn từ đầu

            // với end thì chọn last important word
            int eIndex;
            for (eIndex = tokens.size() - 1; eIndex > -1; eIndex--) {
                if (tokens.get(eIndex).keyword) {
                    break;              // tìm last important token
                }
            }
            while (eIndex > -1 && eIndex < tokens.size() - 1 && tokens.get(eIndex + 1).iPhrase == tokens.get(eIndex).iPhrase) {
                eIndex++;                    // tìm phrase tương ứng
            }
//            fragments[si][1] = eIndex;

            boolean flag = true;
            while (flag && eIndex > -1 && eIndex < tokens.size() - 1) {
                int tIndex = eIndex + 1;
                /// Kiểm tra kết thúc là 1 NP thì nối VP hoặc AP sau đó vào
                boolean endWithNP = tokens.get(eIndex).chunk.endsWith("NP")
                        && (tokens.get(tIndex).chunk.endsWith("VP") || tokens.get(tIndex).chunk.endsWith("AP"));
                /// Kiểm tra kết thúc là 1 VP thì nối NP hoặc AP hoặc VP sau đó vào
                boolean endWithVP = tokens.get(eIndex).chunk.endsWith("VP")
                        && (tokens.get(tIndex).chunk.endsWith("NP") || tokens.get(tIndex).chunk.endsWith("AP") || tokens.get(tIndex).chunk.endsWith("VP"));
                /// Kiểm tra kết thúc là 1 V thì nối cụm VP vào
                boolean endWithVerb = tokens.get(eIndex).posTag.equals("V")
                        && tokens.get(tIndex).chunk.endsWith("PP");
                flag = endWithNP || endWithVP || endWithVerb;
                if (flag) {
                    while (tIndex < tokens.size() - 1 && tokens.get(tIndex + 1).iPhrase == tokens.get(tIndex).iPhrase) {
                        tIndex++;
                    }
                    eIndex = tIndex;
                }
            }

            // dấu câu thì bỏ
            if (eIndex != -1 && tokens.get(eIndex).punctuation) {
                eIndex--;
            }
            fragments[si][1] = eIndex;
        }
    }

    private void completingTheBegin() {
        for (int si = 0; si < sentences.size(); si++) {
            ArrayList<MyToken> sen_si = sentences.get(si).tokensList;
            int sIndex = fragments[si][0];
            int eIndex = fragments[si][1];

            int start = sIndex;
            while (start != -1) {
                /// Kiểm tra Conjunction
                int con1 = -1;
                for (int i = start; i < eIndex; i++) {
                    if (sen_si.get(i).posTag.equals("C")) {
                        con1 = i;
                        break;
                    }
                }
                int con2 = -1;
                if (con1 != -1) {
                    for (int i = con1 + 1; i < eIndex; i++) {
                        if (sen_si.get(i).posTag.equals("C")) {
                            con2 = i;
                            break;
                        }
                    }
                }
                int index = -1;
                if (con2 != -1) {
                    index = Conjunction.getConjunction(sen_si.get(con1).word, sen_si.get(con2).word);
                } 
                if (index == -1 && con1 != -1) {        // không thấy cặp liên từ --> xét 1 liên từ thôi
                    index = Conjunction.getConjunction(sen_si.get(con1).word, null);
                }

                if (index != -1) {      // tìm thấy conjunction
                    boolean hasKeyword = false;
                    if (con2 == -1) {     /// chỉ có 1 liên từ
                        /// A thì bỏ phần trước Conj, B thì bỏ phần sau Conj
                        if ("A".equals(Conjunction.CONJUNCTIONS[index][2])) {
                            for (int i = sIndex; i < con1; i++) {
                                if (sen_si.get(i).keyword) {
                                    hasKeyword = true;
                                    break;
                                }
                            }
                            if (!hasKeyword) {
                                sIndex = con1 + 1;
                            }
                        } else {        // if ("B".equals(Conjunction.CONJUNCTIONS[index][2])) {
                            for (int i = con1 + 1; i < eIndex; i++) {
                                if (sen_si.get(i).keyword) {
                                    hasKeyword = true;
                                    break;
                                }
                            }
                            if (!hasKeyword) {
                                eIndex = con1 - 1;
                            }
                        }
                    } else {        /// cặp liên từ
                        for (int j = con1 + 1; j < con2; j++) {
                            if (sen_si.get(j).keyword) {
                                hasKeyword = true;
                                break;
                            }
                        }
                        if (!hasKeyword) {
                            /// A thì bỏ phần trước Conj, B thì bỏ phần sau Conj
                            if ("A".equals(Conjunction.CONJUNCTIONS[index][2])) {
                                sIndex = con2 + 1;
                            } else {
                                sIndex = con1 + 1;
                                eIndex = con2 - 1;
                            }
                        }
                    }
                    start = -1;
                } else if (con1 != -1) {        // không tìm thấy cặp conj thỏa, tìm conj tiếp theo
                    start = con1 + 1;
                } else {        // không tìm thấy conj nào
                    start = -1;
                }
            }
//            /// đảm bảo cú pháp
//            if (sIndex > 0) {
//                boolean hasV = false;
//                boolean hasN = false;
//                for (int i = sIndex; i <= eIndex; i++) {
//                    MyToken token = tokens.get(i);
//                    if (token.chunk.endsWith("VP")) {
//                        hasV = true;
//                    } else if (token.chunk.endsWith("NP")) {
//                        hasN = true;
//                    }
//                }
//                if (hasN && !hasV) {            /// Có NP nhưng ko có VP
//                    // tìm VP ở trước đó
//                    int i = sIndex - 1;
//                    while (i >= 0 && !tokens.get(i).chunk.equals("B-VP")) {
//                        i--;
//                    }
//                    sIndex = (i < 0) ? sIndex : i;
//                    // tìm tiếp NP trước đó
//                    i--;
//                    while (i >= 0 && !tokens.get(i).chunk.equals("B-NP")) {
//                        i--;
//                    }
//                    sIndex = (i < 0) ? sIndex : i;
//                }
//            }
            fragments[si][0] = sIndex;
            fragments[si][1] = eIndex;
        }
    }

    private void updateChange() {
        // keep only essential fragments
        for (int si = 0; si < sentences.size(); si++) {
            ArrayList<MyToken> sen_si = sentences.get(si).tokensList;
            int sIndex = fragments[si][0];
            int eIndex = fragments[si][1];
            if (sIndex == -1 || eIndex == -1) {
                continue;
            }
            MyToken lastToken = null;       // dấu hết câu
            if (eIndex < sen_si.size() - 1) {
                lastToken = sen_si.get(sen_si.size() - 1);
            }
            for (int i = 0; i < sIndex; i++) {
                sen_si.remove(0);
            }
            for (int i = sen_si.size() - 1; i > eIndex; i--) {
                sen_si.remove(eIndex + 1);
            }
            // update iPosition, iPhrase
            int phr = 0;    // phrase
            for (int p = 0; p < sen_si.size() - 1; p++) {
                MyToken token = sen_si.get(p);
                token.iPosition = p;
                token.iPhrase = phr;
                if (sen_si.get(p + 1).iPhrase != token.iPhrase) {
                    phr++;
                }
            }
            sen_si.get(sen_si.size() - 1).iPhrase = phr;
            if (lastToken != null) {
                lastToken.iPosition = sen_si.size();
                lastToken.iPhrase = phr + 1;
                sen_si.add(lastToken);
            }
        }

        // update score
        for (MySentence sentence : sentences) {
            sentence.updateScore();
        }
    }

    public ArrayList<MySentence> reduction() {
        crfReduction();
        completingTheEnd();
        completingTheBegin();
        updateChange();
        return sentences;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/kinhte/KT1.txt");
        MyReducer re = new MyReducer(sentences);
        sentences = re.reduction();

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
    }
}
