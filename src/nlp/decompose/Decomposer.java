/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template sourceDoc, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.decompose;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import nlp.textprocess.MySentence;
import nlp.textprocess.MyToken;
import nlp.textprocess.MyTokenizer;
import nlp.util.CmdUtil;
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
class Position {

    /**
     * Vị trí câu chứa word
     */
    public int s;

    /**
     * Vị trí word trong câu
     */
    public int p;

    public Position(int s, int p) {
        this.s = s;
        this.p = p;
    }

    @Override
    public String toString() {
        return "<" + "s=" + s + ", p=" + p + '>';
//            return "<" + "s=" + (s + 1) + ", p=" + (p + 1) + '>';
    }

}

class DecomposeNode {

    String label;
    double values[];
    int pnodeLabels[];

    /**
     * List lưu các vị trí của word trong source
     */
    ArrayList<Position> positions;

    /**
     * Vị trí của word trong summary
     */
    Position sum_position;

    public DecomposeNode(String label, ArrayList<Position> positions, Position sum_position) {
        this.label = label;
        this.positions = positions;
        this.sum_position = sum_position;
        values = new double[positions.size()];
        pnodeLabels = new int[positions.size()];
    }

}

/**
 * Lớp phân tích dựa trên văn bản tóm tắt và văn bản nguồn. Sử dụng mô hình
 * Markov ẩn.
 *
 * @author TRUNG
 */
public class Decomposer {

    public static final float TOP_K_SENTENCES = 0.1f;

    static final String[] startCuePhrases = {
        ",",
        //        "-",
        "(",
        "cách",
        "cùng",
        "do",
        "để",
        "gồm",
        "mà",
        "như",
        "nhưng",
        "ở",
        "sau",
        "sau_khi",
        "qua",
        "tại",
        "thay_vì",
        "theo",
        "thuộc",
        "trong",
        "trong_đó",
        "trong_khi",
        "tuy_vậy",
        "về",
        "vì",
        "với"
    };
    static final String[] endCuePhrases = {
        ",",
        ")",
        ":",
        "."
    };

    MyTokenizer tokenizer;
    CmdUtil cmd;

    public Decomposer() {
        tokenizer = new MyTokenizer();
        cmd = new CmdUtil();
    }

    /**
     * Trọng số đường đi.
     *
     * @param p1
     * @param p2
     * @return
     */
    double getLogProb(Position p1, Position p2) {
        double d = 0.0;
        if (p2.s == p1.s) {
            if (p2.p - p1.p == 1) {
                d = 0.25;
            } else if (p2.p - p1.p > 1) {
                d = 0.225;
            } else if (p2.p - p1.p < 0) {
                d = 0.2;
            }
        } else if (p2.s > p1.s) {
            if (p2.s < p1.s + 5) {
                d = 0.15;
            } else {
                d = 0.05;
            }
        } else if (p2.s < p1.s) {
            if (p2.s > p1.s - 5) {
                d = 0.125;
            } else {
                d = 0.05;
            }
        }
        return Math.log10(d);
    }

    /**
     * Thực hiện phân tích so sánh văn bản tóm tắt và văn bản gốc. Tìm các cụm
     * từ trong câu gốc giống với trong câu tóm tắt nhất (bằng HMM).
     *
     * @param tokens Văn bản gốc
     * @param sumDoc Tên văn bản tóm tắt
     * @return Danh sách vị trí các từ được giữ lại (trong tất cả các câu) của
     * văn bản gốc.
     */
    ArrayList<Position> decompose(ArrayList<MySentence> sentences, String sumDoc) {
        String tempSum;
        String[] fileParts = sumDoc.split("/");
        String fName = fileParts[fileParts.length - 1].split("\\.")[0];
        tempSum = "temp/" + fName + ".sum.txt";

        tokenizer.tokenize(sumDoc, tempSum);
        ArrayList<String> sumLines = IOUtil.ReadFileByLine(tempSum);
        ArrayList<DecomposeNode> hmmNodes = new ArrayList<>();      // Hidden Markov Model
        HashMap<String, ArrayList<Position>> map = new HashMap<>();     // map lưu vị trí của word trong source
        for (int s_sum = 0; s_sum < sumLines.size(); s_sum++) {
            String[] words = sumLines.get(s_sum).split("\\s+");
            for (int p_sum = 0; p_sum < words.length; p_sum++) {
                String word = words[p_sum];
                ArrayList<Position> list;
//                if (Punctuation.isPuctuation(word) || tokenizer.stopword.isStopWord(word)) {
//                    list = new ArrayList<>();
//                    list.add(new Position(-100, -100));
//                } else {
                list = map.get(word);
                if (list == null) {
//                    System.out.print(word + " : ");
                    list = new ArrayList<>();
                    for (MySentence sentence : sentences) {
                        for (MyToken token : sentence.tokensList) {
                            if (token.word.toLowerCase().equals(word.toLowerCase())) {
                                list.add(new Position(token.nSentence, token.nPosition));
//                                System.out.print(s + "," + p + "  ;  ");
                            }
                        }
                    }
                    map.put(word, list);
//                    System.out.println();
                }
//                }
                if (!list.isEmpty()) {      // từ trong summary nhưng ko trong source
                    DecomposeNode deNode = new DecomposeNode(word, list, new Position(s_sum, p_sum));
                    if (hmmNodes.isEmpty()) {       // initial node
                        for (int i = 0; i < list.size(); i++) {
                            deNode.values[i] = -Math.log10(list.size());
                            deNode.pnodeLabels[i] = -1;
                        }
                    } else {
                        DecomposeNode prevNode = hmmNodes.get(hmmNodes.size() - 1);
                        for (int i = 0; i < list.size(); i++) {
                            if (prevNode.positions.isEmpty()) {
                                System.out.println(prevNode.label);
                            }
                            deNode.values[i] = prevNode.values[0] + getLogProb(prevNode.positions.get(0), list.get(i));
                            deNode.pnodeLabels[i] = 0;
                            for (int j = 1; j < prevNode.positions.size(); j++) {
                                double g = getLogProb(prevNode.positions.get(j), list.get(i));
                                if (prevNode.values[j] + g > deNode.values[i]) {
                                    deNode.values[i] = prevNode.values[j] + g;
                                    deNode.pnodeLabels[i] = j;
                                }
                            }
                        }
                    }
                    hmmNodes.add(deNode);
                }
            }
        }

        // -------------tracking-------------
        Position[] position_result = new Position[hmmNodes.size()];
        // max của node cuối cùng
        int index = 0;
        DecomposeNode lastNode = hmmNodes.get(hmmNodes.size() - 1);
        for (int i = 1; i < lastNode.pnodeLabels.length; i++) {
            if (lastNode.values[index] < lastNode.values[i]) {
                index = i;
            }
        }
        // track
        for (int i = position_result.length - 1; i >= 0; i--) {
            DecomposeNode node = hmmNodes.get(i);
            position_result[i] = node.positions.get(index);
            index = node.pnodeLabels[index];
        }

        // -------------print result-------------
        ArrayList<Position> positions = new ArrayList<>();
        ArrayList<Position> temp = new ArrayList<>();
        String rs = "";
        String phrase = hmmNodes.get(0).label;
        temp.add(position_result[0]);
        int[] result = new int[sumLines.size()];        // sentence alignment
        for (int i = 0; i < result.length; i++) {
            result[i] = -1;
        }
        for (int i = 1; i < position_result.length; i++) {
            DecomposeNode hmmNode = hmmNodes.get(i);
            if (position_result[i].s == position_result[i - 1].s //){
                    && hmmNode.sum_position.s == hmmNodes.get(i - 1).sum_position.s) {
                phrase += " " + hmmNode.label;
                temp.add(position_result[i]);
                if (i == position_result.length - 1) {
                    phrase += "  (S" + position_result[i - 1].s + ")\n";
//                    System.out.println(s);
                    rs += phrase;
                    positions.addAll(temp);
                    temp.clear();
                    int a = result[hmmNodes.get(i - 1).sum_position.s];
                    if (a == -1) {
                        result[hmmNodes.get(i - 1).sum_position.s] = position_result[i - 1].s;
                    } else if (a != position_result[i - 1].s) {
                        result[hmmNodes.get(i - 1).sum_position.s] = -2;        // combine sentence
                    }
                }
            } else {
                // s chứa nhiều hơn 1 từ và không phải là từ dừng
//                if (phrase.contains(" ") && !tokenizer.stopword.isStopWord(phrase.replaceAll(" ", "_"))) {
                if (phrase.contains(" ")) {
                    phrase += "  (S" + position_result[i - 1].s + ")\n";
//                    System.out.println(s);
                    rs += phrase;
                    positions.addAll(temp);
                    temp.clear();
                    int a = result[hmmNodes.get(i - 1).sum_position.s];
                    if (a == -1) {
                        result[hmmNodes.get(i - 1).sum_position.s] = position_result[i - 1].s;
                    } else if (a != position_result[i - 1].s) {
                        result[hmmNodes.get(i - 1).sum_position.s] = -2;        // combine sentence
                    }
                }

                phrase = hmmNode.label;
                temp.add(position_result[i]);
            }
        }
        System.out.println(rs);
        for (int j = 0; j < result.length; j++) {
            System.out.println(j + " : " + result[j]);
        }

        for (int i = 0; i < positions.size();) {
            boolean flag = false;
            for (int j = 0; j < result.length; j++) {
                if (positions.get(i).s == result[j]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                positions.remove(i);
            } else {
                i++;
            }
        }

        return positions;
    }

    /**
     * Tạo dữ liệu train cho reduction dựa trên bộ decomposer.
     *
     * @param sourceFile
     * @param sumFile
     * @param outFile
     */
    public void createTrainData(String sourceFile, String sumFile, String outFile) {
        ArrayList<TrainData> trainList = new ArrayList<>();
        ArrayList<MySentence> sentences = tokenizer.createTokens(sourceFile);
        ArrayList<MyToken> tokens = MySentence.SentenceToDatum(sentences);
        int nSentence = sentences.size();

        ArrayList<Position> positions = decompose(sentences, sumFile);
        TreeSet<Integer> selectedSentence = new TreeSet<>();
        for (Position position : positions) {
            selectedSentence.add(position.s);
        }

        // setup các từ trong top K câu
        int[] topKSentence = MySentence.getTopKSentence(sentences, Math.round(nSentence * TOP_K_SENTENCES));
        TreeSet<String> wordInTopKSentence = new TreeSet<>();
        for (MyToken token : tokens) {
            for (int j = 0; j < topKSentence.length; j++) {
                if (token.word.length() > 3 && !token.semiStopWord
                        && !token.stopWord && topKSentence[j] == token.nSentence) {
                    wordInTopKSentence.add(token.word);
                }
            }
        }

        // setup train list
        for (MyToken token : tokens) {
            if (selectedSentence.contains(token.nSentence)) {
                TrainData trainData = null;
                for (Position position : positions) {
                    if (token.nSentence == position.s && token.nPosition == position.p) {
                        trainData = new TrainData(token, TrainData.yVALUE.RETAIN);
                    }
                }
                if (trainData == null) {
                    if (token.endOfSentence) {
                        trainData = new TrainData(token, TrainData.yVALUE.RETAIN);
                    } else {
                        trainData = new TrainData(token, TrainData.yVALUE.REMOVE);
                    }
                }
                trainData.isInTopKSens = wordInTopKSentence.contains(trainData.word);
                trainList.add(trainData);
            }
        }
        if (trainList.size() <= 0) {
            return;
        }

        // xác định in cue phrase
        for (int i = 0; i < trainList.size(); i++) {
            boolean sFlag = false;
            TrainData trainData = trainList.get(i);
            int j = i;
            while (!sFlag && j >= 0 && j > i - 7
                    && trainList.get(j).iSentence == trainData.iSentence) {
                for (String phr : startCuePhrases) {
                    if (phr.equals(trainList.get(j).word)) {
                        sFlag = true;
                        break;
                    }
                }
                if (!sFlag) {
                    j--;
                }
            }
            if (j == -1 || trainList.get(j).iSentence != trainData.iSentence
                    || (i > 0 && trainList.get(i - 1).isInCuePhrase)) {
                sFlag = true;
            }
            boolean eFlag = false;
            if (j == i) {
                eFlag = true;
            } else {
                j = i + 1;
                while (!eFlag && j < trainList.size() && j < i + 7
                        && trainList.get(j).iSentence == trainData.iSentence) {
                    for (String phr : endCuePhrases) {
                        if (phr.equals(trainList.get(j).word)) {
                            eFlag = true;
                            break;
                        }
                    }
                    j++;
                }
                if (j == trainList.size() || trainList.get(j).iSentence != trainData.iSentence) {
                    eFlag = true;
                }
            }
            if (sFlag && eFlag && !trainData.endOfSentence) {
                trainData.isInCuePhrase = true;
            }
        }

        // căn lại vị trí tương đối
        int[] nWords = new int[nSentence];  // number of words in each sentence
        int pCounter = 1;       // position counter
        for (int j = 1; j < trainList.size(); j++) {
            TrainData trainData = trainList.get(j);
            if (trainData.iSentence != trainList.get(j - 1).iSentence) {
                nWords[(int) trainList.get(j - 1).iSentence] = pCounter;
                pCounter = 0;
            } else {
                pCounter++;
            }
        }
        nWords[(int) trainList.get(trainList.size() - 1).iSentence] = pCounter;
        for (TrainData trainData : trainList) {
            trainData.iPosition /= ((double) nWords[(int) trainData.iSentence]);
            trainData.iSentence /= ((double) nSentence);
        }

        // xâu kết quả
        String str = "";
        String strSentence = trainList.get(0).toString() + "\n";
        int removeCounter = (trainList.get(0).y == TrainData.yVALUE.REMOVE) ? 1 : 0;
        int wordCounter = 1;
        TrainData tempLast = new TrainData(tokens.get(0));    // dùng làm data tạm thôi (trick)
        tempLast.iSentence = -1;
        trainList.add(tempLast);
        for (int j = 1; j < trainList.size(); j++) {
            TrainData trainData = trainList.get(j);
            if (trainData.iSentence != trainList.get(j - 1).iSentence) {
                if (removeCounter < wordCounter / 2 && removeCounter > 1) {
//                if (removeCounter < 10) {
                    str += strSentence + "\n";        // các câu cách nhau bởi dòng trống
                }
                strSentence = "";
                removeCounter = wordCounter = 0;
            }
            if (trainData.y == TrainData.yVALUE.REMOVE) {
                removeCounter++;
            }
            wordCounter++;
            strSentence += trainData.toString() + "\n";
        }
//        System.out.println(str);
//        IOUtil.WriteToFile(outFile, str, false);
        IOUtil.WriteToFile(outFile, str, true);
    }

    /**
     * Tạo dữ liệu đầu vào cho crf++ reduction.
     *
     * @param tokens
     * @param outFile
     */
    public void createTestData(ArrayList<MyToken> tokens, String outFile) {
//        ArrayList<Datum> data = tokenizer.createTokens(sourceFile);
        ArrayList<MySentence> sentences = MySentence.DatumToSentence(tokens);
        int nSentence = sentences.size();

        // setup các từ trong top K câu
        int[] topKSentence = MySentence.getTopKSentence(sentences, Math.round(nSentence * TOP_K_SENTENCES));
        TreeSet<String> wordInTopKSentence = new TreeSet<>();
        for (MyToken token : tokens) {
            for (int j = 0; j < topKSentence.length; j++) {
                if (token.word.length() > 3 && !token.semiStopWord
                        && !token.stopWord && topKSentence[j] == token.nSentence) {
                    wordInTopKSentence.add(token.word);
                }
            }
        }

        // setup train data list
        ArrayList<TrainData> trainList = new ArrayList<>();
        for (MyToken token : tokens) {
            TrainData trainData = new TrainData(token);
            trainData.isInTopKSens = wordInTopKSentence.contains(trainData.word);
            trainList.add(trainData);
        }

        // xác định in cue phrase
        for (int i = 0; i < trainList.size(); i++) {
            boolean sFlag = false;
            TrainData trainData = trainList.get(i);
            int j = i;
            while (!sFlag && j >= 0 && j > i - 5
                    && trainList.get(j).iSentence == trainData.iSentence) {
                for (String phr : startCuePhrases) {
                    if (phr.equals(trainList.get(j).word)) {
                        sFlag = true;
                        break;
                    }
                }
                if (!sFlag) {
                    j--;
                }
            }
            if (j == -1 || trainList.get(j).iSentence != trainData.iSentence) {
                sFlag = true;
            }
            boolean eFlag = false;
            if (j == i) {
                eFlag = true;
            } else {
                j = i + 1;
                while (!eFlag && j < trainList.size() && j < i + 5
                        && trainList.get(j).iSentence == trainData.iSentence) {
                    for (String phr : endCuePhrases) {
                        if (phr.equals(trainList.get(j).word)) {
                            eFlag = true;
                            break;
                        }
                    }
                    j++;
                }
                if (j == trainList.size() || trainList.get(j).iSentence != trainData.iSentence) {
                    eFlag = true;
                }
            }
            if (sFlag && eFlag && !trainData.endOfSentence) {
                trainData.isInCuePhrase = true;
            }
        }

        // căn lại vị trí tương đối
        int[] nWords = new int[nSentence];  // number of words in each sentence
        int pCounter = 1;       // position counter
        for (int j = 1; j < trainList.size(); j++) {
            TrainData trainData = trainList.get(j);
            if (trainData.iSentence != trainList.get(j - 1).iSentence) {
                nWords[(int) trainList.get(j - 1).iSentence] = pCounter;
                pCounter = 0;
            } else {
                pCounter++;
            }
        }
        nWords[(int) trainList.get(trainList.size() - 1).iSentence] = pCounter;
        for (TrainData trainData : trainList) {
            trainData.iPosition /= ((double) nWords[(int) trainData.iSentence]);
            trainData.iSentence /= ((double) nSentence);
        }

        // xâu kết quả
        String str = trainList.get(0).toString() + "\n";
        for (int j = 1; j < trainList.size(); j++) {
            TrainData trainData = trainList.get(j);
            if (trainData.iSentence != trainList.get(j - 1).iSentence) {
                str += "\n";        // các câu cách nhau bởi dòng trống
            }
            str += trainData.toString() + "\n";
        }
        IOUtil.WriteToFile(outFile, str + "\n", true);
    }

    public void reduction(ArrayList<MyToken> data) {
        String testFile = "temp/temp.test";
        String resultFile = "temp/temp.result";
        createTestData(data, testFile);
        cmd.runCmd(cmd.crf_test(CmdUtil.REDUCTION_MODEL, testFile, resultFile));
        ArrayList<String> lines = IOUtil.ReadFileByLine(resultFile);
        String str = "";
        for (int i = 0, j = 0; i < lines.size() && j < data.size(); i++) {
            String[] features = lines.get(i).split("\\s");
            if (features[0].equals(data.get(j).word)) {
                if (features[features.length - 1].equals(TrainData.yVALUE.REMOVE.toString()) && !data.get(j).endOfSentence) {
                    data.remove(j);
                } else {
                    str += features[0];
                    if (data.get(j).endOfSentence) {
                        str += "\n";
                    } else {
                        str += " ";
                    }
                    j++;
                }
            }
        }
        System.out.println(str);
    }

    public static void main(String[] args) {
        Decomposer decomposer = new Decomposer();
//        IOUtil.DeleteFile("temp/test.txt");
//        decomposer.createTrainData("corpus/Plaintext/vanhoa/VH01.txt", "corpus/Summary/vanhoa/VH01.txt", "temp/train_1.txt");
//        ArrayList<MyToken> data = decomposer.tokenizer.createTokens("corpus/Plaintext/xahoi/XH01.txt");
//        decomposer.createTestData(data, "temp/train_1.txt");
//        decomposer.reduction(data);
//        IOUtil.DeleteFile("temp/train.nlp");
        File file = new File("corpus/Plaintext/");
        String[] directories = file.list();
        int counter = 0;
        for (String d : directories) {
            File directory = new File(file.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents

//            ArrayList<MyToken> data = decomposer.tokenizer.createTokens(directory.getPath() + "/" + files[0].getName());
//            decomposer.createTestData(data, "temp/train_1.txt");
            decomposer.createTrainData("corpus/Plaintext/" + d + "/" + files[0].getName(),
                    "corpus/Summary/" + d + "/" + files[0].getName(), "temp/test.txt");
            decomposer.createTrainData("corpus/Plaintext/" + d + "/" + files[1].getName(),
                    "corpus/Summary/" + d + "/" + files[1].getName(), "temp/test.txt");
//            for (int i = 2; i < files.length; i++) {
//                try {
//                    String name = files[i].getName();
//                    decomposer.createTrainData("corpus/Plaintext/" + d + "/" + name,
//                            "corpus/Summary/" + d + "/" + name, "temp/train.nlp");
//                    counter++;
//                } catch (Exception ex) {
////                continue;
//                    System.out.println("Failure on file " + files[i].getPath() + "!\n\n");
//                }
//            }
        }
        System.out.println("\n" + counter + " văn bản đã được đọc");
    }
}
