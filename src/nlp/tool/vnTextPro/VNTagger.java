package nlp.tool.vnTextPro;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvnpostag.CRFTagger;
import jvnpostag.MaxentTagger;
import jvnpostag.POSTagger;
import nlp.dict.NounAnaphora;
import nlp.dict.Punctuation;
import nlp.dict.Stopword;
import nlp.sentenceExtraction.Datum;
import nlp.sentenceExtraction.IdfScore;
import nlp.util.CmdCommand;
import nlp.util.IOUtil;
import nlp.util.MyStringUtil;

public class VNTagger {

    private static VNTagger instance;
    private static POSTagger tagger;
    private final Stopword stopword = new Stopword();
    private final NounAnaphora nounAnaphora = new NounAnaphora();

    public static VNTagger getInstance() {
        if (instance == null) {
            instance = new VNTagger();
        }
        return instance;
    }

    private VNTagger() {
        this("maxent", "models/vntagger/maxent");
    }

    public VNTagger(String algorithm, String modelRes) {
        init(algorithm, modelRes);
    }

    private void init(String algorithm, String modelRes) {
        if ("maxent".equalsIgnoreCase(algorithm)) {
            tagger = new MaxentTagger(modelRes);
        } else {
            tagger = new CRFTagger(modelRes);
        }
    }

//    public void tag(String inputFile, PrintStream out) {
//        String returnStr = tagger.tagging(new File(inputFile));
//        String[] str = StringUtil.StringToArray(returnStr, new String[]{"\n"});
//        for (String s : str) {
//            out.println(s);
//        }
//    }
    public String tag(String str) {
        return tagger.tagging(str);
    }

    public void tag(String inputFile, String outputFile) {
        String str = VNPreprocessing.prepareTag(inputFile);
        String taggedStr = tag(str).replaceAll(" ", "\n").replaceAll("/", " ");
        IOUtil.WriteToFile(outputFile, taggedStr);
    }

    /**
     * POS tagging
     *
     * @param inputNum
     * @return
     * @throws IOException
     */
    public ArrayList<Datum> tagger(String inputNum) throws IOException {
        String fileNameSource = "corpus/Plaintext/" + inputNum + ".txt";
        String outputFileToken = "data/" + inputNum + "-token.txt";
        String outputFilePre = "temp/" + inputNum + "-presource-temp.txt";
        String outputFileTagger = "data/" + inputNum + "-postag.txt";
        String outputFileChunker = "data/" + inputNum + "-chunk.txt";        //Input Tagger file

        VNPreprocessing.preprocess(fileNameSource, outputFilePre);
        VNTokenizer token = VNTokenizer.getInstance();
        token.tokenize(outputFilePre, outputFileToken);
        tag(outputFileToken, outputFileTagger);

        /// Chay command line cua chuong trinh VietChunker
        /// đã có file -postag.txt
        try {
            CmdCommand.runCommand(inputNum);
        } catch (Exception e) {
            System.out.println("Can't run VietChunker. Error : " + e);
        }

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
        VNTagger ins = VNTagger.getInstance();
//        String fileNameSource = "corpus/Plaintext/test.txt";
//        String strTest = "Quá trình khám xét 6 container này, đội Kiểm soát Hải quan đã phát hiện hàng trăm xe mô tô.";
////        String strTest = "Bố Bách mua thuốc về cho Bách uống. Sau khi uống, anh ấy bị đỏ môi.";
//        IOUtil.WriteToFile(fileNameSource, strTest);

        List<Datum> datum;
        try {
            datum = ins.tagger("1");
//            System.out.println("\n");
//            System.out.println(strTest);
//            System.out.println("");
            System.out.println(datum.toString());
        } catch (IOException ex) {
            Logger.getLogger(VNTagger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
