/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.ArrayList;
import nlp.graph.WordsGraph;
import nlp.sentenceExtraction.Datum;
import nlp.sentenceExtraction.MyTagger;

/**
 *
 * @author Trung
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        String path = "corpus/Plaintext";
        String file;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
//        Synonym synonym = new Synonym();
//        synonym.initSynonymMap();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                file = listOfFile.getName();
                if (file.endsWith(".txt")) {
                    String inputNum = file.split("\\.")[0];
                    try {
                        WordsGraph graph = new WordsGraph();
                        MyTagger tagger = new MyTagger();
                        ArrayList<Datum> data = tagger.getData(inputNum);
                        graph.mainWordGraph(inputNum, data, 120);
                    } catch (IOException e) {
                        System.out.println("Error: " + e);
                    }
                }
            }
        }
    }
}