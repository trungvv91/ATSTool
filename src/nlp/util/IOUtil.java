/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TRUNG
 */
public class IOUtil {

    /**
     * FEFF because this is the Unicode char represented by the UTF-8 byte order
     * mark (EF BB BF). int value = 65279
     */
    public static final String UTF8_BOM = "\uFEFF";

    public static void DeleteFile(String filename) {
        File file = new File(filename);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    public static void AppendFile(String sourceFile, String desFile) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFile, true), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Ghi text ra file. Ghi đè nếu file đã tồn tại.
     *
     * @param filename
     * @param text
     */
    public static void WriteToFile(String filename, String text) {
        File file = new File(filename);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(text);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void RawProcess(String filename) {
        String text = IOUtil.ReadFile(filename);
        text = text.replaceAll("…|(\\.\\.+)", "...")
                .replaceAll("[„“”]", "\"")
                .replaceAll("[‘’]", "'")
                .replaceAll("\\&", " và ")
                .replaceAll("\\/", "-")
                .replaceAll("[–]", "-");
        WriteToFile(filename, text);
    }

    /**
     * Ghi text ra file.
     *
     * @param filename
     * @param text
     * @param append Ghi đè nếu file đã tồn tại hay không
     */
    public static void WriteToFile(String filename, String text, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename, append), StandardCharsets.UTF_8))) {
//            System.out.println(text);
            bw.write(text);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Đọc các line từ file, lưu vào ArrayList. Loại bỏ BOM nếu có. Không đọc
     * dòng empty
     *
     * @param filename
     * @return ArrayList có mỗi phần tử là 1 line
     */
    public static ArrayList<String> ReadFileByLine(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                } else {
//                    lines.add("\n");
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!lines.isEmpty()) {
            // BOM
            line = lines.get(0);
            if (line.startsWith(UTF8_BOM)) {
                lines.set(0, line.substring(1));
            }
        }
        return lines;
    }

    /**
     * Đọc các line từ file, lưu vào ArrayList. Loại bỏ BOM nếu có.
     *
     * @param filename
     * @param readEmptyLine có đọc empty line không
     * @return ArrayList có mỗi phần tử là 1 line
     */
    public static ArrayList<String> ReadFileByLine(String filename, boolean readEmptyLine) {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                } else if (readEmptyLine) {
                    lines.add("");
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!lines.isEmpty()) {
            // BOM
            line = lines.get(0);
            if (line.startsWith(UTF8_BOM)) {
                lines.set(0, line.substring(1));
            }
        }
        return lines;
    }

    /**
     * Đọc toàn bộ file text (dung lượng không lớn lắm). Có trim() file. Loại bỏ
     * BOM nếu có.
     *
     * @param filename
     * @return String chứa text
     */
    public static String ReadFile(String filename) {
        String text = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    text += line.trim() + "\n";
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        // BOM
        if (text.startsWith(UTF8_BOM)) {
            text = text.substring(1);
        }
        return text;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("corpus/Summary1/");
//        File file = new File("corpus/Plaintext1/");
        String[] directories = file.list();
        int counter = 0;
        for (String d : directories) {
            File directory = new File(file.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (int i = 0; i < files.length; i++) {
                try {
                    String name = files[i].getName();
                    String text = IOUtil.ReadFile("corpus/Summary1/" + d + "/" + name);
                    text = text.replaceAll("\\(.*?\\)", "").replaceAll("(\\s)+", "$1");
//                    text = text.replaceAll("…|(\\.\\.+)", "...")
//                            .replaceAll("[„“”]", "\"")
//                            .replaceAll("[‘’]", "'")
//                            .replaceAll("\\&", " và ")
//                            .replaceAll("\\/", "-")
//                            .replaceAll("[–]", "-")
//                            .replaceAll("(\\s)+", "$1");
//                    String[] lines = text.split("\n");
//                    for (String line : lines) {
//                        if (line.matches("(.*?)\\.[^\\s\\.\\d\",;](.*)")) {
//                            System.out.println(name + ": " + line);
//                        }
//                    }
                    System.out.println("corpus/Summary1/" + d + "/" + name + ": \n" + text);
                    IOUtil.WriteToFile("corpus/Summary1/" + d + "/" + name, text);
//                    decomposer.createTrainData("corpus/Plaintext/" + d + "/" + name,
//                            "corpus/Summary/" + d + "/" + name, "data/train.nlp");
                    counter++;
                } catch (Exception ex) {
//                continue;
                    System.out.println("Failure on file " + files[i].getPath() + "!\n\n");
                }
            }
        }
        System.out.println("\n" + counter + " văn bản đã được đọc");
    }
}
