package nlp.evaluation;

import java.io.File;
import java.util.ArrayList;
import nlp.extradata.Synonym;
import nlp.textprocess.WordGraphs;
import nlp.textprocess.MyExtracter;
import nlp.textprocess.MyReducer;
import nlp.textprocess.MySentence;
import nlp.textprocess.MyToken;
import nlp.textprocess.MyTokenizer;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author trung
 */
public class Summarization {

    public Summarization() {
        Synonym.Init();
    }

    private String decoration(ArrayList<MySentence> sentences) {
        String outString = "";
        int nWord = 0;
        for (MySentence sentence : sentences) {
            outString += MyStringUtil.capitalize(sentence.tokensList.get(0).word);
            for (int i = 1; i < sentence.tokensList.size(); i++) {
                MyToken token = sentence.tokensList.get(i);
                if (token.punctuation) {
                    outString += token.word;
                } else {
                    outString += " " + token.word;
                    nWord++;
                }
            }
            outString += "\n";
        }
        outString = outString.trim();
        outString = outString.replaceAll("_", " ").replaceAll("([\\p{L}\\d])\\- ", "$1 - ");
        System.out.println(outString);
        System.out.println("Số từ: " + nWord);
        System.out.println("Số chữ: " + outString.split("\\s+").length);

        return outString;
    }

    /**
     * Tóm tắt 1 xâu
     *
     * @param sourceText
     * @param wordMax - số chữ (không phải số từ)
     * @return
     */
    public String summarize(String sourceText, int wordMax) {
        String inputFile = "temp/displayFile.txt";
        IOUtil.WriteToFile(inputFile, sourceText);
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens(inputFile);
        MyReducer re = new MyReducer(sentences);
        WordGraphs wg = new WordGraphs(re.reduction());
        MyExtracter se = new MyExtracter(wg.generateSentences());
        sentences = se.extract(wordMax);
        return decoration(sentences);
    }

    /**
     * Tóm tắt file
     *
     * @param input file gốc
     * @param output file tóm tắt
     * @param wordMax - số chữ (không phải số từ)
     */
    public void summarize(String input, String output, int wordMax) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens(input);
        MyReducer re = new MyReducer(sentences);
        WordGraphs wg = new WordGraphs(re.reduction());
        MyExtracter se = new MyExtracter(wg.generateSentences());
        sentences = se.extract(wordMax);
        String outString = decoration(sentences);
        IOUtil.WriteToFile(output, outString);
    }

    public static void main(String[] args) {
        Summarization sum = new Summarization();
//        MyTokenizer tokenizer = new MyTokenizer();
//        long start = System.nanoTime();
//        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/kinhte/KT01.txt");
//        long end = System.nanoTime();
//        System.out.println((end - start) / 1e6);
//        MyReducer re = new MyReducer(sentences);
//        WordGraphs wg = new WordGraphs(re.reduction());
//        MyExtracter se = new MyExtracter(wg.generateSentences());
//        sentences = se.extract(100);
//        String outString = sum.decoration(sentences);
//        IOUtil.WriteToFile("corpus/AutoSummary/kinhte/KT01.txt", outString);
        
        sum.summarize("corpus/Plaintext/kinhte/KT01.txt", "corpus/AutoSummary/kinhte/KT01.txt", 90);
        
//        File sourceFile = new File("corpus/Plaintext");
//        String[] directories = sourceFile.list();
//        int counter = 0;
//
//        for (String d : directories) {
//            File directory = new File("corpus/Plaintext/" + d);
//            if (directory.isFile()) {
//                continue;
//            }
//            File[] files = directory.listFiles();    // Reading directory contents
//            for (File file : files) {
//                sum.summarize(file.getPath(), "corpus/AutoSummary/" + d + "/" + file.getName(), 100);
//                counter++;
//            }
//        }
//        System.out.println("\n" + counter + " văn bản đã được tóm tắt");
    }
}
