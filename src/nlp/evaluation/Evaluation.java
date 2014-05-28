/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nlp.textprocess.MyTokenizer;
import nlp.util.IOUtil;

/**
 * Dùng ROUGE-N
 *
 * @author trung
 */
public class Evaluation {

    MyTokenizer tokenizer;

    public Evaluation() {
        tokenizer = new MyTokenizer();
    }

    public void createBaseLine() {
        File dir = new File("corpus/Plaintext/");
        String[] directories = dir.list();
        int counter = 0;
        for (String d : directories) {
            File directory = new File(dir.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (File file : files) {
                try {
                    String name = file.getName();
                    String text = IOUtil.ReadFile(directory.getPath() + "/" + name);
                    String[] sentences = text.split("\n");
                    String s = IOUtil.ReadFile("corpus/Summary/" + d + "/" + name);
                    int nWords = s.split("\\s+").length;        // lấy số từ theo văn bản tóm tắt tay
                    System.out.println(nWords);
                    ArrayList<Integer> list = new ArrayList<>();
                    for (int i = 0; i < sentences.length; i++) {
                        list.add(i);
                    }
                    Collections.shuffle(list);
                    int[] arrIndices = new int[sentences.length];
                    for (int i = 0; i < list.size(); i++) {
                        arrIndices[i] = list.get(i);
                    }
                    String str = "";
//                    int maxWords = IOUtil.ReadFile(file.getPath()).split("\\s+").length;
//                    maxWords = (maxWords < 120) ? maxWords : 120;
                    int wordCounter = 0, i = 0;
                    while (wordCounter < nWords * 0.8) {
                        String sentence = sentences[arrIndices[i++]];
                        wordCounter += sentence.split("\\s+").length + 2;
                        str += sentence + "\n";
                    }
                    System.out.println(wordCounter + "\n");
                    IOUtil.WriteToFile("corpus/BaselineSummary/" + d + "/" + name, str);
                    counter++;
                } catch (Exception ex) {
                    System.out.println("Failure on file " + file.getPath() + "!\n\n");
                }
            }
        }
        System.out.println("\n" + counter + " văn bản đã được tạo");
    }

    private String getTokenString(String summaryFile) {
        String[] parts = summaryFile.split("[/\\\\]");
        String tempFile = "temp/" + parts[parts.length - 1] + ".temp";
        tokenizer.tokenize(summaryFile, tempFile);
        String str = IOUtil.ReadFile(tempFile);
        IOUtil.DeleteFile(tempFile);
//        str = str.replaceAll("[,\\.]", "");
//        str = str.replaceAll("[,:;!?\\.\\(\\)\\[\\]]", "");
        return str.toLowerCase();
    }

    private int[] rouge_1(String candidateSum, String referenceSum) {
        String[] canWords = candidateSum.split("\\s+");
        List<String> canGram = Arrays.asList(canWords);
        String[] refWords = referenceSum.split("\\s+");
        List<String> refGram = Arrays.asList(refWords);
        int count = refGram.size();
        int count_match = 0;
        for (String w : canGram) {
            if (refGram.contains(w)) {
                count_match++;
            }
        }
        return new int[]{count_match, count};
    }

    private int[] rouge_2(String candidateSum, String referenceSum) {
        String[] canWords = candidateSum.split("\\s+");
        List<String> canGram = new ArrayList<>();
        for (int i = 0; i < canWords.length - 1; i++) {
            canGram.add(canWords[i] + "##" + canWords[i + 1]);
        }
        String[] refWords = referenceSum.split("\\s+");
        List<String> refGram = new ArrayList<>();
        for (int i = 0; i < refWords.length - 1; i++) {
            refGram.add(refWords[i] + "##" + refWords[i + 1]);
        }
        int count = refGram.size();
        int count_match = 0;
        for (String w : canGram) {
            if (refGram.contains(w)) {
                count_match++;
            }
        }
        return new int[]{count_match, count};
    }

    private int[] rouge_3(String candidateSum, String referenceSum) {
        String[] canWords = candidateSum.split("\\s+");
        List<String> canGram = new ArrayList<>();
        for (int i = 0; i < canWords.length - 2; i++) {
            canGram.add(canWords[i] + "##" + canWords[i + 1] + "##" + canWords[i + 2]);
        }
        String[] refWords = referenceSum.split("\\s+");
        List<String> refGram = new ArrayList<>();
        for (int i = 0; i < refWords.length - 3; i++) {
            refGram.add(refWords[i] + "##" + refWords[i + 1] + "##" + refWords[i + 2]);
        }
        int count = refGram.size();
        int count_match = 0;
        for (String w : canGram) {
            if (refGram.contains(w)) {
                count_match++;
            }
        }
        return new int[]{count_match, count};
    }

    private int[] rouge_4(String candidateSum, String referenceSum) {
        String[] canWords = candidateSum.split("\\s+");
        List<String> canGram = new ArrayList<>();
        for (int i = 0; i < canWords.length - 3; i++) {
            canGram.add(canWords[i] + "##" + canWords[i + 1] + "##" + canWords[i + 2] + "##" + canWords[i + 3]);
        }
        String[] refWords = referenceSum.split("\\s+");
        List<String> refGram = new ArrayList<>();
        for (int i = 0; i < refWords.length - 3; i++) {
            refGram.add(refWords[i] + "##" + refWords[i + 1] + "##" + refWords[i + 2] + "##" + refWords[i + 3]);
        }
        int count = refGram.size();
        int count_match = 0;
        for (String w : canGram) {
            if (refGram.contains(w)) {
                count_match++;
            }
        }
        return new int[]{count_match, count};
    }

    /**
     *
     * @param ngrams 1-4
     * @param candidateSumFile
     * @param referenceSumFiles
     * @return
     */
    public double[] rouge(int ngrams, String candidateSumFile, String[] referenceSumFiles) {
        String candidateSum = getTokenString(candidateSumFile);
        double count_match = 0;
        int count = 0;
        for (String referenceSumFile : referenceSumFiles) {
            String referenceSum = getTokenString(referenceSumFile);
            int[] count_rouge;
            switch (ngrams) {
                case 1:
                    count_rouge = rouge_1(candidateSum, referenceSum);
                    break;
                case 2:
                    count_rouge = rouge_2(candidateSum, referenceSum);
                    break;
                case 3:
                    count_rouge = rouge_3(candidateSum, referenceSum);
                    break;
                case 4:
                    count_rouge = rouge_4(candidateSum, referenceSum);
                    break;
                default:
                    count_rouge = null;
            }
            count_match += count_rouge[0];
            count += count_rouge[1];
        }
        double recall = count_match / count;
        double precision = count_match / (candidateSum.split("\\s+").length + 1 - ngrams);
        double F = (2 * recall * precision);
        F = (F == 0) ? 0 : F / (recall + precision);
        return new double[]{recall, precision, F};
    }

    /**
     *
     * @param candidateSumFile
     * @param referenceSumFiles
     * @return
     */
    public double[][] rouge(String candidateSumFile, String[] referenceSumFiles) {
        String candidateSum = getTokenString(candidateSumFile);
        double[][] result = new double[4][];
        double[] count_match = new double[4];
        int[] count = new int[4];
        for (String referenceSumFile : referenceSumFiles) {
            String referenceSum = getTokenString(referenceSumFile);
            int[] count_rouge;

            count_rouge = rouge_1(candidateSum, referenceSum);
            count_match[0] += count_rouge[0];
            count[0] += count_rouge[1];

            count_rouge = rouge_2(candidateSum, referenceSum);
            count_match[1] += count_rouge[0];
            count[1] += count_rouge[1];

            count_rouge = rouge_3(candidateSum, referenceSum);
            count_match[2] += count_rouge[0];
            count[2] += count_rouge[1];

            count_rouge = rouge_4(candidateSum, referenceSum);
            count_match[3] += count_rouge[0];
            count[3] += count_rouge[1];
        }
        for (int i = 0; i < 4; i++) {
            double recall = count_match[i] / count[i];
            double precision = count_match[i] / (candidateSum.split("\\s+").length - i);
            double F = (2 * recall * precision);
            F = (F == 0) ? 0 : F / (recall + precision);
            result[i] = new double[]{recall, precision, F};
        }
        return result;
    }

    public static void main(String[] args) {
        Evaluation eval = new Evaluation();

//        String candidateSum = "đội kiểmsoát thuộc cục hảiquan";
//        String referenceSum = "cục hảiquan đã pháthiện lôhàng được đóng trong container nữa";
//        int[] count_rouge = eval.rouge_1(candidateSum, referenceSum);
//        double count_match = count_rouge[0];
//        int count = count_rouge[1];
//
//        double recall = count_match / count;
//        double precision = count_match / (candidateSum.split("\\s+").length);
//        double F = (2 * recall * precision);
//        F = (F == 0) ? 0 : F / (recall + precision);
//        System.out.println(recall + "\t " + precision + "\t" + F);

//        int[] rouge_1 = eval.rouge(1"pulses may ease schizophrenic voices", 
//                "magnetic pulse series sent through brain may ease schizophrenic voices");
//        double[] rouge = eval.rouge(4, "corpus/AutoSummary/kinhte/KT01.txt", new String[]{"corpus/Summary/kinhte/KT01.txt"});
//        System.out.println("Recall = " + rouge[0] + "\tPrecision = " + rouge[1] + "\tF = " + rouge[2]);
//        double[] rouge = eval.rouge(1, "temp/1.txt", new String[]{"temp/2.txt"});
//        double[] rouge = eval.rouge(1, "corpus/AutoSummary/kinhte/KT01.txt", new String[]{"corpus/Summary/kinhte/KT01.txt"});
//        System.out.println("Recall = " + rouge[0] + "\tPrecision = " + rouge[1] + "\tF = " + rouge[2]);
//        double[][] rouge = eval.rouge("temp/2.txt", new String[]{"temp/1.txt"});
//        for (int i = 0; i < 4; i++) {
//            double[] r = rouge[i];
//            System.out.println("Recall = " + r[0] + "\tPrecision = " + r[1] + "\tF = " + r[2]);
//        }
        
        eval.createBaseLine();
        File dir = new File("corpus/BaselineSummary/");
        String[] directories = dir.list();
        int counter = 0;
        double[][] avg = new double[4][3];
        String output = "";
        for (String d : directories) {
            File directory = new File(dir.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (int k = 0; k < 2; k++) {
                try {
                    String name = files[k].getName();
                    double[][] rouge = eval.rouge(dir.getPath() + "/" + d + "/" + name,
                            new String[]{"corpus/Summary/" + d + "/" + name});
                    output += d + "/" + name + ":\n";
                    for (int i = 0; i < 4; i++) {
                        double[] r = rouge[i];
                        for (int j = 0; j < r.length; j++) {
                            avg[i][j] += r[j];
                        }
//                        System.out.print("corpus/AutoSummary/" + d + "/" + name + ":");
//                        System.out.println("Recall = " + r[0] + "\tPrecision = " + r[1] + "\tF = " + r[2]);
                        output += "Rouge-" + (i + 1) + ": \tRecall = " + r[0] + "\tPrecision = " + r[1] + "\tF = " + r[2] + "\n";
                    }
                    output += "\n";
                    counter++;
                } catch (Exception ex) {
                    System.out.println("Failure on file " + files[k].getPath() + "!\n\n");
                }
            }
        }
        System.out.println("\n" + counter + " văn bản đã được đánh giá");

        output += "Average:\n";
        for (int i = 0; i < 4; i++) {
            output += "Rouge-" + (i + 1) + ": \tRecall = " + avg[i][0] / counter + "\tPrecision = " + avg[i][1] / counter + "\tF = " + avg[i][2] / counter + "\n";
        }
        IOUtil.WriteToFile("temp/my_eval_baseline.txt", output);
        System.out.println(output);
    }
}
