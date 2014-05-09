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

public class CmdUtil {

    public static final String CHUNKER_MODEL = "chunk.model";
    public static final String REDUCTION_MODEL = "reduct.model";

    String[] command;
    String fileExt;

    public CmdUtil() {
        command = new String[1];
        command[0] = "cmd";
        fileExt = ".bat";
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
            Logger.getLogger(CmdUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Command line của CRF++. Chẳng hạn với chương trình VietChunker model là
     * file model.chunk, file input là .tagged.txt, đã được tách dòng
     *
     * @param model model file
     * @param inputFile each line of the form: Vũ_Dư	Np
     * @param outputFile each line of the form: Vũ_Dư	Np	B-NP
     * @return xâu command cho đầu vào của hàm runCmd
     */
    public String crf_test(String model, String inputFile, String outputFile) {
        return "crf_test -m " + model + " ../" + inputFile + " > ../" + outputFile;
    }

    /**
     * Command line của chương trình vnSentDetector, đầu vào là file encode in
     * UTF-8 without BOM
     *
     * @param inputFile
     * @param outputFile
     * @return xâu command cho đầu vào của hàm runCmd
     */
    public String vnSentDetector(String inputFile, String outputFile) {
        return "vnSentDetector" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
    }

    /**
     * Command line của chương trình vnTokenizer
     *
     * @param inputFile file đã được Sentence Detected
     * @param outputFile
     * @return xâu command cho đầu vào của hàm runCmd
     */
    public String vnTokenizer(String inputFile, String outputFile) {
        String cmd = "vnTokenizer" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
        if (outputFile.endsWith(".xml")) {
            cmd += " -xo";
        }
        return cmd;
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
     * @return
     */
    public String vnTagger(String inputFile, String outputFile) {
        String cmd = "vnTagger" + fileExt + " -i ../" + inputFile + " -o ../" + outputFile;
        if (outputFile.endsWith(".txt")) {
            cmd += " -u -p";
        }
        return cmd;
    }

    /**
     * Results of the test will be outputed to the standard console. Note that
     * the test file need to be a plain text file in which syllables are
     * separated by underscores, words are separated by spaces.
     *
     * @param inputFile
     * @return
     */
    public String testTaggedFile(String inputFile) {
        return "vnTagger" + fileExt + " -t ../" + inputFile;
    }

    public static void main(String[] args) {
        CmdUtil cmdCommand = new CmdUtil();
        cmdCommand.runCmd(cmdCommand.vnTokenizer("temp/1.txt", "temp/1.sd.txt"));
//        cmdCommand.vnTokenizer("temp/1.sd.txt", "temp/1.tok.xml");
//        cmdCommand.vnTokenizer("temp/1.sd.txt", "temp/1.tok.txt");
//        cmdCommand.vnTagger("temp/0.sd.txt", "temp/0.tagged.txt");
//        cmdCommand.crf_test("temp/0.tagged.line.txt", "temp/0.chunk.txt");
//        cmdCommand.testTaggedFile("temp/1.tagged.txt");
    }
}
