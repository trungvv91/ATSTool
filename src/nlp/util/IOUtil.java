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

    /**
     * Đọc các line từ file, lưu vào ArrayList
     * @param filename
     * @return 
     */
    public static ArrayList<String> ReadFile(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lines;
    }

    public static ArrayList<String[]> ReadFileByWord(String filename) {
        ArrayList<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    lines.add(line.split("\\s+"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lines;
    }
}
