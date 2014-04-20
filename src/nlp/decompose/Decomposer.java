/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template sourceDoc, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.decompose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nlp.dict.Stopword;
import nlp.sentenceExtraction.Datum;
import nlp.sentenceExtraction.MyTagger;
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class Decomposer {

    MyTagger tagger;

    public Decomposer() {
        tagger = new MyTagger();
    }

    class Position {

        /**
         * Vị trí câu chứa word
         */
        public int s;

        /**
         * Vị trí word trong câu
         */
        public int p;

//        /**
//         * Vị trí câu trong summary
//         */
//        public int s_sum;
//
//        /**
//         * Vị trí trong câu summary
//         */
//        public int p_sum;
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

    public ArrayList<Position> decompose(String sourceDoce, String sumDoc) {
        String tempSum;
        String[] fileParts = sumDoc.split("/");
        String fName = fileParts[fileParts.length - 1].split("\\.")[0];
        tempSum = "data/" + fName + ".sum.txt";

        tagger.tokenize(sumDoc, tempSum);
        ArrayList<String> sumLines = IOUtil.ReadFileByLine(tempSum);
        ArrayList<Datum> data = tagger.getData(sourceDoce);
        ArrayList<DecomposeNode> hmmNodes = new ArrayList<>();      // Hidden Markov Model
        Map<String, ArrayList<Position>> map = new HashMap<>();     // map lưu vị trí của word trong source
        for (int s_sum = 0; s_sum < sumLines.size(); s_sum++) {
            String[] words = sumLines.get(s_sum).split("\\s+");
            for (int p_sum = 0; p_sum < words.length; p_sum++) {
                String word = words[p_sum];
                ArrayList<Position> list;
//                if (Punctuation.isPuctuation(word) || tagger.stopword.isStopWord(word)) {
//                    list = new ArrayList<>();
//                    list.add(new Position(-100, -100));
//                } else {
                list = map.get(word);
                if (list == null) {
//                    System.out.print(word + " : ");
                    list = new ArrayList<>();
                    for (Datum datum : data) {
                        if (datum.word.toLowerCase().equals(word.toLowerCase())) {
                            list.add(new Position(datum.iSentence, datum.iPosition));
//                                System.out.print(s + "," + p + "  ;  ");
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
                if (phrase.contains(" ") && !tagger.stopword.isStopWord(phrase.replaceAll(" ", "_"))) {
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
            }
        }
        System.out.println(rs);
        for (int j = 0; j < result.length; j++) {
            System.out.println(j + " : " + result[j]);
        }

        return positions;
    }

    public static void main(String[] args) {

        Decomposer decomposer = new Decomposer();
        for (int i = 2; i < 3; i++) {
            ArrayList<Position> decompose = decomposer.decompose("corpus/Plaintext/" + i + ".txt", "corpus/Summary/" + i + ".txt");
            for (int j = 0; j < decompose.size(); j++) {
                System.out.println(decompose.get(j).toString());
            }
        }
    }
}
