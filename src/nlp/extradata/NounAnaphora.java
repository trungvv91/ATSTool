/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

import java.util.ArrayList;
import nlp.textprocess.MySentence;
import nlp.util.IOUtil;

/**
 *
 * @author tien
 */
public class NounAnaphora {

    /**
     * "anh + ấy"
     */
    private final ArrayList<String> listNounAnaphora1;

    /**
     * "mẹ của Bách"
     */
    private final ArrayList<String> listNounAnaphora2;

    private final String filename1 = "data/nounAnaphoric1.txt";
    private final String filename2 = "data/nounAnaphoric2.txt";

    public ArrayList<String> getNounAnaphora1() {
        return listNounAnaphora1;
    }

    public ArrayList<String> getListNounAnaphora2() {
        return listNounAnaphora2;
    }

    public NounAnaphora() {
        listNounAnaphora1 = IOUtil.ReadFileByLine(filename1);
        listNounAnaphora2 = IOUtil.ReadFileByLine(filename2);
    }

    /**
     * check "anh + ấy"
     *
     * @param s
     * @return
     */
    public boolean isNounAnaphora1(String s) {
//        String s1 = s.toLowerCase();
        for (String na : listNounAnaphora1) {
            if (s.equals(na)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check "mẹ của Bách"
     *
     * @param s
     * @return
     */
    public boolean isNounAnaphora2(String s) {
//        String s1 = s.toLowerCase();
        for (String na : listNounAnaphora2) {
            if (s.equals(na)) {
                return true;
            }
        }
        return false;
    }

    /// Java manipulates objects by reference, but it passes object references to methods by value
    /**
     * Phân giải đồng tham chiếu cho danh từ là chủ ngữ
     *
     * @param sentences
     */
    public void nounAnaphoring(ArrayList<MySentence> sentences) {
        /// Noun Anaphoric 1
        for (int i = 1; i < sentences.size(); i++) {
            MySentence seni = sentences.get(i);
            int[] indices = seni.getSubject();
            for (int j = indices[0]; j <= indices[1]; j++) {
                switch (seni.dataList.get(j).posTag) {
                    case "P":
                        MySentence seni_1 = sentences.get(i - 1);
                        int[] indices_1 = seni_1.getSubject();
                        /// anaphoring pronoun
                        seni.deletePhrase(indices[0], indices[1]);
                        for (int k = indices_1[1]; k >= indices_1[0]; k--) {
                            seni.dataList.add(indices[0], seni_1.dataList.get(k));
                        }
                        return;
                    case "Np":
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        NounAnaphora na = new NounAnaphora();
        System.out.println(na.isNounAnaphora1("ông_ấy"));
    }
}
