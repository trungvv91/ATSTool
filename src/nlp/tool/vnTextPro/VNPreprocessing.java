/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.tool.vnTextPro;

import java.util.ArrayList;
import nlp.dict.Punctuation;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

/**
 *
 * @author Manh Tien
 */
public class VNPreprocessing {

    /**
     * @param args the command line arguments
     */
    private static VNPreprocessing instance;

    public static VNPreprocessing getInstance() {
        if (instance == null) {
            instance = new VNPreprocessing();
        }
        return instance;
    }

    /**
     * inputFile - tốt nhất là mỗi line 1 câu
     *
     * @param inputFile
     * @param outputFile
     */
    public static void preprocess(String inputFile, String outputFile) {
        ArrayList<String> lines = IOUtil.ReadFile(inputFile);
        String str = "";
        for (String line : lines) {
            String newLine = line
                    .replaceAll("[\\(\\[].+?[\\)\\]]", "") /// bỏ các phần trong ngoặc
                    .replaceAll("…|(\\.\\.+)", "...")
                    .replaceAll("[„“”]", "\"")
                    .replaceAll("[‘’]", "'")
                    .replaceAll("[–]", "\\-")
                    .replaceAll("(\\.\\.\\.|[!,:;\\?\\-<>]) ", " $1 ")
                    .replaceAll("([\"])", " $1 ")
                    .replaceAll("\\s+", " ")
                    .trim()
                    .replaceAll("\\s*\\.$", " .\n");
            str += newLine;
        }
        // BOM
        if ((int) str.charAt(0) == 65279) {
            str = str.substring(1);
        }

        IOUtil.WriteToFile(outputFile, str);
    }

    public static String prepareTag(String inputFile) {
        ArrayList<String> lines = IOUtil.ReadFile(inputFile);
        String str = "";
        for (String line : lines) {
            String[] tokens = line.replaceAll("(\\p{L}) \\- (\\p{L})", "$1-$2").split(" ");
            int i = 0;
            while (Punctuation.isPuctuation(tokens[i])) {
                i++;
            }
            String[] words = tokens[i].split("_");
            if (words.length < 2 || MyStringUtil.isUncapitalize(words[1])) {
                tokens[i] = MyStringUtil.unCapitalize(tokens[i]);
            }
            for (String token : tokens) {
                str += token + " ";
            }
            str += "\n";
        }
        return str;
    }

    public static void main(String[] args) {
        preprocess("corpus/Plaintext/1.txt", "temp/1-presource-temp.txt");
        VNTokenizer token = VNTokenizer.getInstance();
        token.tokenize("temp/1-presource-temp.txt", "data/1-token.txt");
    }
}
