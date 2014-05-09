package nlp.textprocess;

import nlp.extradata.IdfScore;
import java.util.ArrayList;
import java.util.List;
import nlp.extradata.Punctuation;
import nlp.extradata.Stopword;
import nlp.util.CmdUtil;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

/**
 * Lớp xử lý đầu tiên, đọc văn bản nguồn và tách thành các token sử dụng thư viện tách
 * từ của anh Lê Hồng Phương. Các token được gán thuộc tính tương ứng và tính tf_idf.
 *
 * @author TRUNG
 */
public class MyTokenizer {

    final Stopword stopword;
    final CmdUtil cmd;
    final IdfScore idfScore;

    public MyTokenizer() {
        cmd = new CmdUtil();
        stopword = new Stopword();
        idfScore = new IdfScore();
    }

    /**
     * Tiền xử lý cho Sentence Detection. Thay các dấu câu là ký tự lạ. Bỏ phần
     * phụ trong ngoặc đơn.
     *
     * @param inputFile - file original source .txt
     * @param outputFile - file .edit
     */
    private void preprocess(String inputFile, String outputFile) {
        String text = IOUtil.ReadFile(inputFile);
//        text = text.replaceAll("…|(\\.\\.+)", "...")
//                .replaceAll("[„“”]", "\"")
//                .replaceAll("[‘’]", "'")
//                .replaceAll("\\&", " và ")
//                .replaceAll("\\/", "-")
//                .replaceAll("[–]", "-");
        text = text.replaceAll("\\(.*?\\)", "");       // bỏ phần trong ngoặc
        IOUtil.WriteToFile(outputFile, text);
    }

    /**
     * Tiền xử lý cho MyTokenizer. Thay các từ viết hoa thành viết thường để tag
     * được.
     *
     * @param inputFile - file .sd
     * @param outputFile - file .sd.edited
     */
    private void midprocess(String inputFile, String outputFile) {
        ArrayList<String> lines = IOUtil.ReadFileByLine(inputFile);
        String text = "";
        String[] nounPrefixes = {"Bộ", "Cục", "Đội", "Viện", "Vụ"};
        for (String line : lines) {
            String newLine = "";
            String[] tokens = line.split("\\s+");
            for (String token : tokens) {
                for (String nounPrefix : nounPrefixes) {
                    if (token.equals(nounPrefix)) {
                        token = MyStringUtil.unCapitalize(token);
                    }
                }
                newLine += token + " ";
            }
            text += newLine + "\n";
        }
        IOUtil.WriteToFile(outputFile, text);
    }

    /**
     * Tiền xử lý cho Vietchunker. Sửa file tagged thành các line làm đầu vào
     * cho VietChunker.
     *
     * @param inputFile Mỗi từ được tag ở dạng: bệnh_viện/N
     * @param outputFile Mỗi dòng có dạng: bệnh_viện N
     */
    private void postprocess(String inputFile, String outputFile) {
        String text = IOUtil.ReadFile(inputFile);
        text = text.replaceAll("trong_đó/A", "trong_đó/C")
                .replaceAll("khi/N", "khi/E")
                .replaceAll("phân_khối/V", "phân_khối/N")
                .replaceAll("([\\(\\)])/[MA]", "$1/$1")
                .replaceAll("\n", "\n\n")
                .replaceAll(" ", "\n")
                .replaceAll("/", "\t");
//        String[] lines = text.split("\n");
//        for (String line : lines) {
//            int i = line.length();
//        }
        IOUtil.WriteToFile(outputFile, text);
    }

    /**
     * Chạy vnTokenizer (được gọi bởi bộ decomposer)
     *
     * @param inputFile
     * @param outputFile
     */
    public void tokenize(String inputFile, String outputFile) {
        String editFile = outputFile + ".edit";
        preprocess(inputFile, editFile);
        String sdFile = outputFile + ".sd";
        cmd.runCmd(cmd.vnSentDetector(editFile, sdFile));
        String sdEditFile = outputFile + ".sd.edit";
        midprocess(sdFile, sdEditFile);
        cmd.runCmd(cmd.vnTokenizer(sdEditFile, outputFile));
//        IOUtil.DeleteFile(editFile);
//        IOUtil.DeleteFile(sdFile);
//        IOUtil.DeleteFile(sdEditFile);
    }

    /**
     * Tạo các đối tượng token từ văn bản ban đầu. Xác định POS, vị trí câu,
     * tính tf-idf, tf-isf...
     *
     * @param inputFile e.g. corpus/Plaintext/1.txt
     * @return
     */
    public ArrayList<MyToken> createTokens(String inputFile) {
        String[] fileParts = inputFile.split("/");
        String fName = fileParts[fileParts.length - 1].split("\\.")[0];
        String inputFilePreprocessed = "temp/" + fName + ".edit.txt";
        String outputFileSD = "temp/" + fName + ".sd.txt";
        String outputFileSDEdited = "temp/" + fName + ".sd.edit.txt";
        String outputFileToken = "temp/" + fName + ".tok.txt";
        String outputFileTagger = "temp/" + fName + ".tagged.txt";
        String outputFileTaggerSD = "temp/" + fName + ".tagged.line.txt";
        String outputFileChunker = "temp/" + fName + ".chunk.txt";

        // run command line
        preprocess(inputFile, inputFilePreprocessed);
        String vnSentDetector = cmd.vnSentDetector(inputFilePreprocessed, outputFileSD);
        cmd.runCmd(vnSentDetector);
        midprocess(outputFileSD, outputFileSDEdited);
        String vnTokenizer = cmd.vnTokenizer(outputFileSDEdited, outputFileToken);
        String vnTagger = cmd.vnTagger(outputFileSDEdited, outputFileTagger);
        cmd.runCmd(vnTokenizer, vnTagger);
        postprocess(outputFileTagger, outputFileTaggerSD);
        cmd.runCmd(cmd.crf_test(CmdUtil.CHUNKER_MODEL, outputFileTaggerSD, outputFileChunker));

        List<String> lines = IOUtil.ReadFileByLine(outputFileChunker, true);           // each line of the form: Vũ_Dư	Np	B-NP
        ArrayList<MyToken> tokens = new ArrayList<>();

        // gán thuộc tính cho các Token
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
                tokens.get(tokens.size() - 1).endOfSentence = true;
                continue;
            }
            String[] parts = line.split("\\s+");
            MyToken token = new MyToken(parts[0], parts[1], parts[2]);

            if (MyStringUtil.isUpperWord(token.word)) {
                token.posTag = "Np";
            }
            if (!token.posTag.equals("Np")) {
                token.word = token.word.toLowerCase();
            }
            if (token.posTag.equals("X")) {
                token.posTag = "N";
            }
//            if (d.chunk.equals("O") && !Punctuation.isPuctuation(d.word)) {
//                d.chunk = "B-" + d.posTag;
//            }

            if (token.chunk.startsWith("B-")) {
                phraseCounter++;
                if (token.chunk.equals("B-PP")) {
                    for (int index = lineCounter + 1; lines.get(index).endsWith("NP"); index++) {
                        String nextLine = lines.get(index);
                        nextLine = nextLine.substring(0, nextLine.length() - 4) + "I-PP";
                        lines.set(index, nextLine);
                    }
                }
            }
            token.iPosition = tokenCounter++;
            token.iPhrase = phraseCounter;
            token.iSentence = sentenceCounter;
            token.stopWord = stopword.isStopWord(token.word);

            if (token.posTag.equals("E") || token.posTag.equals("M") || token.posTag.equals("T")
                    || token.posTag.equals("C") || token.posTag.equals("R")) {
                token.semiStopWord = true;
            } else if (Punctuation.isPuctuation(token.word)) {
                token.punctuation = true;
//                if (Punctuation.isEndOfSentence(d.word)) {
//                    d.endOfSentence = true;
//                }
                if (!token.word.equals(",") || token.chunk.startsWith("B-")) {
//                    d.posTag = d.word;
                    token.chunk = "O";
                    if (lineCounter < nLines - 1) {
                        lines.set(lineCounter + 1, lines.get(lineCounter + 1).replace("I-", "B-"));      /// continue --> begin nếu trước đó là dấu câu
                    }
                }
            }

            tokens.add(token);
        }

        /// Calculate tf-isf tf-idf
        idfScore.tf_isf(tokens);

        return tokens;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MyToken> tokens = tokenizer.createTokens("corpus/Plaintext/1.txt");
        String str = "";
        for (MyToken token : tokens) {
            str += token.toString() + "\n";
            System.out.println(token.toString());
            if (token.endOfSentence) {
                str += "\n";
                System.out.println("");
            }
        }
        IOUtil.WriteToFile("temp/1.chunk.edited.txt", str);
    }
}
