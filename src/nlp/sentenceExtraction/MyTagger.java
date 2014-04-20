package nlp.sentenceExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nlp.dict.Punctuation;
import nlp.dict.Stopword;
import nlp.util.CmdCommand;
import nlp.util.IOUtil;

public class MyTagger {

    public final Stopword stopword;
    CmdCommand cmd;

    public MyTagger() {
        stopword = new Stopword();
        cmd = new CmdCommand();
    }

    /**
     * Thay các dấu câu là ký tự lạ.
     *
     * @param inputFile
     * @param outputFile
     */
    private void preprocess(String inputFile, String outputFile) {
        String text = IOUtil.ReadFile(inputFile);
        text = text.replaceAll("…|(\\.\\.+)", "...")
                .replaceAll("[„“”]", "\"")
                .replaceAll("[‘’]", "'")
                .replaceAll("[–]", "\\-");
        IOUtil.WriteToFile(outputFile, text);
    }

    /**
     * Sửa file tagged thành các line làm đầu vào cho VietChunker.
     *
     * @param inputFile Mỗi từ được tag ở dạng: bệnh_viện/N
     * @param outputFile Mỗi dòng có dạng: bệnh_viện N
     */
    private void postprocess(String inputFile, String outputFile) {
        String text = IOUtil.ReadFile(inputFile);
        text = text.replaceAll("\n", "\n\n").replaceAll(" ", "\n").replaceAll("/", "\t");
        IOUtil.WriteToFile(outputFile, text);
    }

    public void tokenize(String inputFile, String outputFile) {
        preprocess(inputFile, outputFile + ".1");
        cmd.vnSentDetector(outputFile + ".1", outputFile + ".2");
        cmd.vnTokenizer(outputFile + ".2", outputFile);
        IOUtil.DeleteFile(outputFile + ".1");
        IOUtil.DeleteFile(outputFile + ".2");
    }

    public void chunker(String inputFile, String outputFile) {
        preprocess(inputFile, outputFile);
        cmd.vnSentDetector(outputFile, outputFile);
        cmd.vnTokenizer(outputFile, outputFile);
        cmd.vnTagger(outputFile, outputFile);
        postprocess(outputFile, outputFile);
        cmd.crf_test(outputFile, outputFile);
    }

    /**
     * Tạo các đối tượng datum từ văn bản ban đầu. Xác định POS, vị trí câu,
     * tính tf-idf, ...
     *
     * @param inputFile corpus/Plaintext/1.txt
     * @return
     */
    public ArrayList<Datum> getData(String inputFile) {
        String[] fileParts = inputFile.split("/");
        String fName = fileParts[fileParts.length - 1].split("\\.")[0];
        String inputFilePreprocessed = "data/" + fName + ".txt";
        String outputFileSD = "data/" + fName + ".sd.txt";
        String outputFileToken = "data/" + fName + ".tok.txt";
        String outputFileTagger = "data/" + fName + ".tagged.txt";
        String outputFileTaggerSD = "data/" + fName + ".tagged.line.txt";
        String outputFileChunker = "data/" + fName + ".chunk.txt";

        // run command line
        preprocess(inputFile, inputFilePreprocessed);
        cmd.vnSentDetector(inputFilePreprocessed, outputFileSD);
        cmd.vnTokenizer(outputFileSD, outputFileToken);
        cmd.vnTagger(outputFileSD, outputFileTagger);
        postprocess(outputFileTagger, outputFileTaggerSD);
        cmd.crf_test(outputFileTaggerSD, outputFileChunker);

        List<String> lines = IOUtil.ReadFileByLine(outputFileChunker, true);           // each line of the form: Vũ_Dư	Np	B-NP
        ArrayList<Datum> data = new ArrayList<>();
        int nLines = lines.size();
        int sentenceCounter = 0;
        int phraseCounter = -1;
        int tokenCounter = 0;

        for (int lineCounter = 0; lineCounter < nLines; lineCounter++) {
            String line = lines.get(lineCounter);
            if (line.equals("")) {
                sentenceCounter++;
                phraseCounter = -1;
                tokenCounter = 0;
                continue;
            }
            String[] parts = line.split("\\s+");
            Datum d = new Datum(parts[0], parts[1], parts[2]);

            if (!d.posTag.equals("Np")) {
                d.word = d.word.toLowerCase();
            }
            if (d.chunk.equals("O") && !Punctuation.isPuctuation(d.word)) {
                d.chunk = "B-" + d.posTag;
            }

            if (d.chunk.startsWith("B-")) {
                phraseCounter++;
            }
            d.iPosition = tokenCounter++;
            d.iPhrase = phraseCounter;
            d.iSentence = sentenceCounter;
            d.stopWord = stopword.isStopWord(d.word);

            if (d.posTag.equals("E") || d.posTag.equals("M") || d.posTag.equals("T")
                    || d.posTag.equals("C") || d.posTag.equals("R")) {
                d.semiStopWord = true;
            } else if (Punctuation.isPuctuation(d.word)) {
//                if (Punctuation.isEndOfSentence(d.word)) {
//                    if (phraseCounter >= 1) {       // hết 1 câu
//                        sentenceCounter++;
//                        phraseCounter = -1;
//                        tokenCounter = 0;
//                    } else {                        // ???
//                        Datum prev = data.get(data.size() - 1);
//                        d.iPosition = prev.iPosition + 1;
//                        d.iPhrase = prev.iPhrase + 1;
//                        d.iSentence = prev.iSentence;
//                    }
//                }
                if (!d.chunk.equals("O")) {
                    d.chunk = "O";
                    if (lineCounter < nLines - 1) {
                        lines.set(lineCounter + 1, lines.get(lineCounter + 1).replace("I-", "B-"));      /// continue --> begin nếu trước đó là dấu câu
                    }
                }
            }

            data.add(d);
        }

        /// Calculate tf-idf tf_idf
        tf_isf(data);

        return data;
    }

    /**
     * Tính chỉ số tf-isf.
     *
     * @param data
     */
    public void tf_isf(ArrayList<Datum> data) {
        System.out.println("Start of isf-scoring...");
        int S = data.get(data.size() - 1).iSentence + 1;      // the total number of sentences in the document
        Map<String, int[]> tf_map = new HashMap<>();       // map có key là từ, value là mảng S+1 giá trị, với giá trị cuối lưu sum(arr)
        for (int i = 0; i < data.size(); i++) {
            Datum di = data.get(i);
            int[] arr_tf;
            String key = di.word + "#" + di.posTag;
            if (tf_map.containsKey(key)) {
                arr_tf = tf_map.get(key);
                arr_tf[di.iSentence]++;
            } else {
                arr_tf = new int[S + 1];
                arr_tf[di.iSentence] = 1;
            }
            arr_tf[S]++;
            tf_map.put(key, arr_tf);
        }
        for (Datum datum : data) {
            if (Punctuation.isPuctuation(datum.word) || stopword.isStopWord(datum.word)) {
                continue;
            }
            int[] arr_tf = tf_map.get(datum.word + "#" + datum.posTag);
            int tf = arr_tf[datum.iSentence];
            double isf = Math.log10(S / (arr_tf[S] + 0.0));
            datum.tf_isf = tf * isf;
        }
        System.out.println("End of isf-scoring...");

//        System.out.println("Start of idf-scoring...");
//        IdfScore idfScore = new IdfScore();
//        Map<String, Double> maps = idfScore.getIdfScoreMap(data);
//        for (Datum di : data) {
//            if (Punctuation.isPuctuation(di.word) || stopword.isStopWord(di.word)) {
//                continue;
//            }
//            if (di.tf == 0) {
//                int count = 1;
//                int i = data.indexOf(di);
//                for (int j = i + 1; j < data.size(); j++) {
//                    Datum dj = data.get(j);
//                    if (di.equals(dj)) {
//                        count++;
//                        dj.tf = -i;     /// lưu lại chỉ số của thằng giống nó đã tính ở trước
//                    }
//                }
//                di.tf = count;
//            } else if (di.tf < 0) {
//                di.tf = data.get(-di.tf).tf;      /// chỉ số được lưu dùng ở đây
//            }
//            di.idf = maps.get(di.word);
//            di.tf_idf = di.idf * di.tf;
//        }
//        System.out.println("End of idf-scoring...");
    }

    public static void main(String[] args) {
        MyTagger vnTagger = new MyTagger();
        ArrayList<Datum> data = vnTagger.getData("corpus/Plaintext/2.txt");
        String str = "";
        for (Datum datum : data) {
            str += datum.toString() + "\n";
            System.out.println(datum.toString());
        }
        IOUtil.WriteToFile("data/2.chunk.edited.txt", str);
    }
}
