
import java.util.ArrayList;
import nlp.graph.WordsGraph;
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
    
    static String decoration(ArrayList<MySentence> sentences) {
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
     * @param sourceText
     * @param wordMax
     * @return
     */
    public static String summarize(String sourceText, int wordMax) {
        String inputFile = "temp/displayFile.txt";
        IOUtil.WriteToFile(inputFile, sourceText);
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens(inputFile);
        MyReducer re = new MyReducer(sentences);
        WordsGraph wg = new WordsGraph(re.reduction());
        MyExtracter se = new MyExtracter(wg.combination());
        sentences = se.extract(wordMax);
        return decoration(sentences);
    }

    /**
     * Tóm tắt file
     *
     * @param input
     * @param output
     * @param wordMax
     */
    public static void summarize(String input, String output, int wordMax) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens(input);
        MyReducer re = new MyReducer(sentences);
        WordsGraph wg = new WordsGraph(re.reduction());
        MyExtracter se = new MyExtracter(wg.combination());
        sentences = se.extract(wordMax);
        String outString = decoration(sentences);
        IOUtil.WriteToFile(output, outString);
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/chinhtri/CT01.txt");
        MyReducer re = new MyReducer(sentences);
//        MyExtracter se = new MyExtracter(re.reduction());
        WordsGraph wg = new WordsGraph(re.reduction());
        MyExtracter se = new MyExtracter(wg.combination());
        sentences = se.extract(120);
        decoration(sentences);
    }
}
