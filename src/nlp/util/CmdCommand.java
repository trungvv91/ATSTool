/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.util;

/**
 *
 * @author Trung
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

class SyncPipe implements Runnable {

    public SyncPipe(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }

    @Override
    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length = 0; -1 != (length = istrm_.read(buffer));) {
                ostrm_.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final OutputStream ostrm_;
    private final InputStream istrm_;
}

public class CmdCommand {

    String[] command = new String[1];
    String fileExt;

    public CmdCommand() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            command[0] = "cmd";
            fileExt = ".bat";
        } else {
            command[0] = "xterm";
            fileExt = ".sh";
        }
    }

    /**
     * Chạy một tập các câu lệnh command line
     *
     * @param cmdLines
     */
    public void runCmd(String... cmdLines) {
        Runtime runtime = Runtime.getRuntime();
        Process p;
        try {
            p = runtime.exec(command);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            try (PrintWriter stdin = new PrintWriter(p.getOutputStream())) {
                stdin.println("cd cmd");
                for (String cmdLine : cmdLines) {
                    stdin.println(cmdLine);
                }
            }
            System.out.println("Return code = " + p.waitFor());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CmdCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Chạy command line của chương trình VietChunker với file .tagged.txt, đã
     * được tách dòng
     *
     * @param inputFile each line of the form: Vũ_Dư	Np
     * @param outputFile each line of the form: Vũ_Dư	Np	B-NP
     */
    public void crf_test(String inputFile, String outputFile) {
        String cmd = "crf_test.exe -m model_crf2 < ../" + inputFile + " > ../" + outputFile;
        runCmd(cmd);
    }

    /**
     * Chạy command line của chương trình vnSentDetector, đầu vào là file encode
     * in UTF-8 without BOM
     *
     * @param inputFile
     * @param outputFile
     */
    public void vnSentDetector(String inputFile, String outputFile) {
        String cmd = "vnSentDetector" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
        runCmd(cmd);
    }

    /**
     *
     * @param inputFile file đã được Sentence Detected
     * @param outputFile
     */
    public void vnTokenizer(String inputFile, String outputFile) {
        String cmd = "vnTokenizer" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
        if (outputFile.endsWith(".xml")) {
            cmd += " -xo";
        }
        runCmd(cmd);
    }

    /**
     * By default, syllables of compound words are separated by spaces, you can
     * use option -u to separate them by underscore (_) character. If you want
     * that the result file is a plain text instead of an XML file, use the
     * option -p.
     *
     * @param inputFile file đã được Sentence Detected
     * @param outputFile The tagset in use contains 17 main lexical tags: 1. Np
     * - Proper noun 2. Nc - Classifier 3. Nu - Unit noun 4. N - Common noun 5.
     * V - Verb 6. A - Adjective 7. P - Pronoun 8. R - Adverb 9. L - Determiner
     * 10. M - Numeral 11. E - Preposition 12. C - Subordinating conjunction 13.
     * CC - Coordinating conjunction 14. I - Interjection 15. T - Auxiliary,
     * modal words 16. Y - Abbreviation 17. Z - Bound morphemes 18. X - Unknown.
     * 19. Delimiters and punctuations.
     */
    public void vnTagger(String inputFile, String outputFile) {
        String cmd = "vnTagger" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
        if (outputFile.endsWith(".txt")) {
            cmd += " -u -p";
        }
        runCmd(cmd);
    }

    /**
     * Results of the test will be outputed to the standard console. Note that
     * the test file need to be a plain text file in which syllables are
     * separated by underscores, words are separated by spaces.
     *
     * @param inputFile
     */
    public void testTaggedFile(String inputFile) {
        String cmd = "vnTagger" + fileExt + " -t ../" + inputFile;
        runCmd(cmd);
    }

    public static void main(String[] args) {
        CmdCommand cmdCommand = new CmdCommand();
//        cmdCommand.vnSentDetector("data/1.txt", "data/1.sd.txt");
//        cmdCommand.vnTokenizer("data/1.sd.txt", "data/1.tok.xml");
//        cmdCommand.vnTokenizer("data/1.sd.txt", "data/1.tok.txt");
//        cmdCommand.vnTagger("data/0.sd.txt", "data/0.tagged.txt");
//        cmdCommand.crf_test("data/0.tagged.line.txt", "data/0.chunk.txt");
//        cmdCommand.testTaggedFile("data/1.tagged.txt");
    }
}
