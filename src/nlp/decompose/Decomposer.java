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
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class Decomposer {

    String sourceDoc;
    String sumDoc;
    String tempSource;
    String tempSum;
    String decomposeFile;

    class Position {

        /**
         * Vị trí câu chứa word
         */
        int s;

        /**
         * Vị trí word trong câu
         */
        int p;

        public Position(int s, int p) {
            this.s = s;
            this.p = p;
        }

    }

    class DeNode {

        String label;
        ArrayList<Position> positions;
        double values[];
        int pnodeLabels[];

        public DeNode(String label, ArrayList<Position> positions) {
            this.label = label;
            this.positions = positions;
            values = new double[positions.size()];
            pnodeLabels = new int[positions.size()];
        }

    }

    public Decomposer(String sourceDoc, String sumDoc) {
        this.sourceDoc = sourceDoc;
        this.sumDoc = sumDoc;
        String[] paths = sourceDoc.split("/");
        String[] names = paths[paths.length - 1].split("\\.");
        tempSource = "temp/" + names[0] + "-source-temp" + "." + names[1];
        paths = sumDoc.split("/");
        names = paths[paths.length - 1].split("\\.");
        tempSum = "temp/" + names[0] + "-sum-temp" + "." + names[1];
        decomposeFile = "temp/" + names[0] + "-decom-temp" + "." + names[1];

    }

    void preprocess(String input, String output) {
        ArrayList<String> lines = IOUtil.ReadFile(input);
        String s = "";
        for (String line : lines) {
            String[] ss = line.toLowerCase()
                    .replaceAll("…|\\.\\.\\.", " ")
                    .replaceAll("[„“”\"]", " ")
                    .replaceAll("[‘’']", " ")
                    .replaceAll("[–]", "-")
                    .replaceAll("[()\\[\\]]", " ")
                    .replaceAll("[!,:;\\?\\-] ", " ")
                    .replaceAll("\\s+", " ")
                    .replaceAll("\\.(\\s)*", "\n")
                    .split("\n");
            for (String s1 : ss) {
                s += s1.trim() + "\n";
            }
        }
        // BOM
        if ((int) s.charAt(0) == 65279) {
            s = s.substring(1);
        }
        IOUtil.WriteToFile(output, s);
    }

    void createTempFiles() {
        preprocess(sourceDoc, tempSource);
        preprocess(sumDoc, tempSum);
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

    void decompose() {
        Map<String, ArrayList<Position>> map = new HashMap<>();
        ArrayList<String> lines = IOUtil.ReadFile(tempSum);
        ArrayList<String> sourceLines = IOUtil.ReadFile(tempSource);
        ArrayList<DeNode> hmmNodes = new ArrayList<>();
        for (String line : lines) {
            String[] words = line.split(" ");
            for (String word : words) {
                ArrayList<Position> list = map.get(word);
                if (list == null) {
//                    System.out.print(word + " : ");
                    list = new ArrayList<>();
                    for (int s = 0; s < sourceLines.size(); s++) {
                        String[] sourceWords = sourceLines.get(s).split(" ");
                        for (int p = 0; p < sourceWords.length; p++) {
                            if (sourceWords[p].equals(word)) {
                                list.add(new Position(s, p));
//                                System.out.print(s + "," + p + "  ;  ");
                            }
                        }
                    }
                    map.put(word, list);
//                    System.out.println();
                }
                if (list.size() > 0) {
                    DeNode deNode = new DeNode(word, list);
                    if (hmmNodes.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            deNode.values[i] = -Math.log10(list.size());
                            deNode.pnodeLabels[i] = -1;
                        }
                    } else {
                        DeNode prevNode = hmmNodes.get(hmmNodes.size() - 1);
                        for (int i = 0; i < list.size(); i++) {
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
        Position[] result = new Position[hmmNodes.size()];
        // max
        int index = 0;
        DeNode lastNode = hmmNodes.get(hmmNodes.size() - 1);
        for (int i = 1; i < lastNode.pnodeLabels.length; i++) {
            if (lastNode.values[index] < lastNode.values[i]) {
                index = i;
            }
        }
        // track
        for (int i = result.length - 1; i >= 0; i--) {
            DeNode node = hmmNodes.get(i);
            result[i] = node.positions.get(index);
            index = node.pnodeLabels[index];
        }

        // -------------print result-------------
        Stopword stopword = new Stopword();
        String rs = "";
        String s = hmmNodes.get(0).label;
        for (int i = 1; i < result.length; i++) {
            if (result[i].s == result[i - 1].s) {
                s += " " + hmmNodes.get(i).label;
            } else {
                if (s.contains(" ") && !stopword.isStopWord(s.replaceAll(" ", "_"))) {
                    s += "  (S" + (result[i - 1].s + 1) + ")\n";
//                    System.out.println(s);
                    rs += s;
                }

                s = hmmNodes.get(i).label;
            }
        }
        s += " (S" + (result[result.length - 1].s + 1) + ")";
//        System.out.println(s);
        rs += s;
        System.out.println(rs);
        IOUtil.WriteToFile(decomposeFile, rs);
    }

    public static void main(String[] args) {
        for (int i = 1; i < 6; i++) {
            Decomposer decomposer = new Decomposer("corpus/Plaintext/" + i + ".txt", "corpus/Summary/" + i + ".txt");
            decomposer.createTempFiles();
            decomposer.decompose();
        }
    }
}
