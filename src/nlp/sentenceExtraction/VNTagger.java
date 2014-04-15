package nlp.sentenceExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nlp.dict.NounAnaphora;
import nlp.dict.Punctuation;
import nlp.dict.Stopword;
import nlp.sentenceExtraction.Datum;
import nlp.sentenceExtraction.IdfScore;
import nlp.util.CmdCommand;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

public class VNTagger {

    private final Stopword stopword;
    private final NounAnaphora nounAnaphora;

    public VNTagger() {
        stopword = new Stopword();
        nounAnaphora = new NounAnaphora();
    }

    /**
     * Thay các dấu câu là ký tự lạ, sửa encode with UTF-8 without BOM.
     *
     * @param inputFile
     * @param outputFile
     */
    public void preprocess(String inputFile, String outputFile) {
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
        // bỏ BOM ở đầu đi
        if ((int) str.charAt(0) == 65279) {
            str = str.substring(1);
        }

        IOUtil.WriteToFile(outputFile, str);
    }

    /**
     * Sửa file tagged thành các line làm đầu vào cho VietChunker.
     *
     * @param inputFile
     * @param outputFile
     */
    public void postprocess(String inputFile, String outputFile) {
        ArrayList<String> lines = IOUtil.ReadFile(inputFile);
        String str = "";
        for (String line : lines) {
            str += line + " ";
        }
        str = str.replaceAll(" ", "\n").replaceAll("/", " ");
        IOUtil.WriteToFile(outputFile, str);
    }

    /**
     * Tạo các đối tượng datum từ văn bản ban đầu. Xác định POS, vị trí câu,
     * tính tf-idf, ...
     *
     * @param inputNum
     * @return
     */
    public ArrayList<Datum> tagger(String inputNum) {
        String inputFile = "corpus/Plaintext/" + inputNum + ".txt";
        String inputFilePreprocessed = "data/" + inputNum + ".txt";
        String outputFileSD = "data/" + inputNum + ".sd.txt";
        String outputFileToken = "data/" + inputNum + ".tok.txt";
        String outputFileTagger = "data/" + inputNum + ".tagged.txt";
        String outputFileTaggerSD = "data/" + inputNum + ".tagged.sd.txt";
        String outputFileChunker = "data/" + inputNum + ".chunk.txt";

        preprocess(inputFile, inputFilePreprocessed);
        CmdCommand cmd = new CmdCommand();
        cmd.vnSentDetector(inputFilePreprocessed, outputFileSD);
        cmd.vnTokenizer(outputFileSD, outputFileToken);
        cmd.vnTagger(outputFileSD, outputFileTagger);
        postprocess(outputFileTagger, outputFileTaggerSD);
        cmd.crf_test(outputFileTaggerSD, outputFileChunker);

        List<String> lines = IOUtil.ReadFile(outputFileChunker);           // each line of the form: Vũ_Dư	Np	I-NP
        ArrayList<Datum> datums = new ArrayList<>();
        int nLines = lines.size();
        int sentenceCounter = 0;
        int phrase = -1;         // set iphrase of a word

        for (int lineCounter = 0, datumCounter = 0; lineCounter < nLines; lineCounter++, datumCounter++) {
//            System.out.println("old: " + datums.get(i).tf);
            String line = lines.get(lineCounter);
            String[] parts = line.split("\\s");
            Datum d = new Datum(parts[0], parts[1]);

            d.iSentence = sentenceCounter;
            d.chunk = parts[2];
            if (d.chunk.contains("B-")) {
                phrase++;
            }
            d.iPhrase = phrase;

            if (nounAnaphora.isNounAnaphora1(d.word)) {
                d.posTag = "P";
            } else if (d.word.equals("ấy") || d.word.equals("này") || d.word.equals("đó") || d.word.equals("ta")) {
                d.posTag = "Nb";
            } else if (d.word.equals("họ")) {
                d.posTag = "P";
            } else if (d.word.equals("của")) {      // mẹ của Bách
                if (nounAnaphora.isNounAnaphora1(datums.get(datumCounter - 1).word)) {
                    datums.get(datumCounter - 1).posTag = "N";
                }
            } else if (d.word.equals("nó")) {
                d.posTag = "P";
                if (datums.get(datumCounter - 1).word.equals("chúng")) {
                    datums.get(datumCounter - 1).posTag = "Nc";
                }
            } else if (d.word.equals("anh_ấy") || d.word.equals("anh_ta") || d.word.equals("chị_ta")) {      /// StringUtils.uncapitalize
                String[] split = d.word.split("_");
                d.word = split[1];
                d.posTag = "Nb";
                Datum d1 = new Datum(split[0], "P");
                d1.iSentence = d.iSentence;
                d1.chunk = d.chunk;
                d1.iPhrase = d.iPhrase;
                datums.add(d1);
                datumCounter++;
                d.chunk = "I-NP";
            }

            if (d.posTag.equals("Np")) {        /// ca sỹ Ông_Cao_Thắng
                if (datumCounter > 0 && MyStringUtil.isCapitalize(datums.get(datumCounter - 1).word)) {
                    d.word = datums.get(datumCounter - 1).word + "_" + d.word;
                    d.chunk = datums.get(datumCounter - 1).chunk;
                    datums.remove(datumCounter - 1);
                    datumCounter--;
                }
            } else if (stopword.isStopWord(d.word)) {  // && !"Np".equals(d.posTag)) {
                d.stopWord = true;
            }

            if (Punctuation.isEndOfSentence(d.word)) {
                sentenceCounter++;
                phrase = -1;
            } else if (d.posTag.equals("E") || d.posTag.equals("M") || d.word.equals("không")) {
                d.semiStopWord = true;
            }
            if (Punctuation.isPuctuation(d.word)) {
                d.chunk = "O";      /// dấu câu
                if (lineCounter < nLines - 1) {
                    lines.set(lineCounter + 1, lines.get(lineCounter + 1).replace("I-", "B-"));      /// continue --> begin nếu trước đó là dấu câu
                }
            }

//            System.out.println("old: " + datums.get(i).posTag + " vs. new: " + d.posTag);
//            datums.set(i, d);
            datums.add(d);
        }

        /// Calculate tf-idf score
        System.out.println("Start of idf-scoring...");
        IdfScore idfScore = new IdfScore();
        Map<String, Double> maps = idfScore.getIdfScoreMap(datums);
        for (Datum di : datums) {
            if (di.tf == 0) {
                int count = 1;
                int i = datums.indexOf(di);
                for (int j = i + 1; j < datums.size(); j++) {
                    Datum dj = datums.get(j);
                    if (di.equals(dj)) {
                        count++;
                        dj.tf = -i;     /// lưu lại chỉ số
                    }
                }
                di.tf = count;
            } else if (di.tf < 0) {
                di.tf = datums.get(-di.tf).tf;      /// chỉ số được lưu dùng ở đây
            }
            di.idf = maps.get(di.word.toLowerCase());
            di.score = di.idf * di.tf;
        }
        System.out.println("End of idf-scoring...");

        return datums;
    }

    public static void main(String[] args) {
        VNTagger vnTagger = new VNTagger();
        vnTagger.tagger("1");
    }
}
