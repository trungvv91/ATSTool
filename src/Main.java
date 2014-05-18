
import java.io.File;
import nlp.util.IOUtil;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Trung
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("corpus/Plaintext/");
        String[] directories = file.list();
        int counter = 0;
        for (String d : directories) {
            File directory = new File(file.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
//            for (int i = 0; i < files.length; i++) {
            for (int i = 0; i < 2; i++) {
                File input = files[i];
                String s = IOUtil.ReadFile(input.getPath());
                Summarization.summarize(input.getPath(), "corpus/AutoSummary/" + d + "/" + input.getName(), (int) (s.split("\\s+").length * 0.3));
                counter++;
            }
//            
        }
        System.out.println("\n" + counter + " văn bản đã được tóm tắt");
    }
}
