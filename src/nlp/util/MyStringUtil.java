package nlp.util;

import java.io.File;
import java.util.ArrayList;
import nlp.textprocess.MyTokenizer;

public class MyStringUtil {

    static public String ArrayToString(String[] str, String separator) {
        String resultStr = "";
        for (int i = 0; i < str.length; i++) {
            if (i != str.length - 1) {
                resultStr += str[i] + separator + " ";
            } else {
                resultStr += str[i];
            }
        }
        return resultStr;
    }

    static public String[] StringToArray(String str, String... separator) {
        String separatorRegex = "[" + ArrayToString(separator, "") + "]";
        return str.split(separatorRegex);
    }

    public static boolean isStartWithANumber(String s) {
        return !s.isEmpty() && Character.isDigit(s.charAt(0));
    }

    public static boolean isCapitalize(String s) {
        return !s.isEmpty() && Character.isUpperCase(s.charAt(0));
    }

    public static boolean isUncapitalize(String s) {
        return !s.isEmpty() && Character.isLowerCase(s.charAt(0));
    }

    public static boolean isUpperWord(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String capitalize(String s) {
        return s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String unCapitalize(String s) {
        return s.isEmpty() ? "" : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static void main(String[] args) {
//        System.out.println(MyStringUtil.unCapitalize("Ong_ay"));

        // test ROUGE-N
//        -	0.468339526915418	0.2750940373034472	0.21215760423618638	0.1808100760407686
//        ArrayList<double[]> list = new ArrayList<>();
//        list.add(new double[]{0.4254778535569637, 0.2169522004593181, 0.1594407498623455, 0.13464801414090985});
//        list.add(new double[]{0.47434107957395133, 0.2888213639955094, 0.23395860136206847, 0.20454708155534296});
//        list.add(new double[]{0.4260688932282349, 0.20818693998756724, 0.1357868317319514, 0.1052940491031289});
//        list.add(new double[]{0.47693530170580917, 0.2968773979266219, 0.2397293439583897, 0.20868023538460378});
//        list.add(new double[]{0.4279563888054276, 0.2220595766095053, 0.15471219700708938, 0.12056144655866419});
//        list.add(new double[]{0.4534226382752276, 0.2594286071460027, 0.19855248060803157, 0.1714979904403446});
//        list.add(new double[]{0.4531747630706439, 0.2568589887985683, 0.2008756263506407, 0.17220323651171177});
//        list.add(new double[]{0.46023432951236704, 0.2506870321052883, 0.18182457450468334, 0.15327422438610952});
//        list.add(new double[]{0.47511627178097543, 0.28405942856784844, 0.2222347759549775, 0.1888203861917134});
//        list.add(new double[]{0.49250881714766687, 0.29800921630590144, 0.23088976852305196, 0.19993049394456253});
//
//        double[] avg = new double[4];
//        for (double[] ds : list) {
//            for (int i = 0; i < ds.length; i++) {
//                avg[i] += ds[i];
//            }
//        }
//        for (int i = 0; i < avg.length; i++) {
//            System.out.println(avg[i]/list.size() + "\t");
//        }
        MyTokenizer tokenizer = new MyTokenizer();
        File dir = new File("corpus/Summary/");
        String[] directories = dir.list();
        int counter = 0;
        String output = "";
        for (String d : directories) {
            File directory = new File(dir.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (File file : files) {
                try {
                    output += file.getParent() + "\\" + file.getName() + ":\t";
                    String text = IOUtil.ReadFile(file.getAbsolutePath());
                    int nUnits = text.split("\\s+").length;
                    tokenizer.tokenize(file.getAbsolutePath(), "temp/out.temp");
                    text = IOUtil.ReadFile("temp/out.temp");
                    int nWords = text.replaceAll("[,?!\\.]", "").split("\\s+").length;
                    int nSens = text.split("\n+").length;
                    float avgWordsPerSens = nWords / (float) nSens;
                    output += nUnits + "\t" + nWords + "\t" + nSens + "\t" + ((int) (avgWordsPerSens * 10)) / 10.0;
                    output += "\n";
                    counter++;
                } catch (Exception ex) {
                    System.out.println("Failure on file " + file.getPath() + "!\n\n");
                }
            }
        }
        System.out.println("\n" + counter + " văn bản đã được đọc");

//        IOUtil.WriteToFile("temp/my_corpus.txt", output);
        System.out.println(output);

    }
}
