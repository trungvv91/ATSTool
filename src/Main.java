/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.ArrayList;
import nlp.graph.WordsGraph;
import nlp.textprocess.MyToken;
import nlp.textprocess.MyTokenizer;

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
                    WordsGraph.graphing(file, 120);
                }
            }
        }
    }
}
