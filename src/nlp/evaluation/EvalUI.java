/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.evaluation;

import java.io.File;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import nlp.util.IOUtil;

/**
 *
 * @author TRUNG
 */
public class EvalUI extends javax.swing.JFrame {

    final JFileChooser fc;
    final Evaluation eval;
    final DecimalFormat decimalFormat;
    final Summarization sum;

    /**
     * Creates new form EvalUI
     */
    public EvalUI() {        
        decimalFormat = new DecimalFormat("#.######");
        eval = new Evaluation();
        sum = new Summarization();

        initComponents();
        
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File("corpus"));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        txtAutoSum.setEnabled(false);
        lbAutoSum.setEnabled(false);
        btnAutoSum.setEnabled(false);
    }

    private void summarizeFolder(String source, String autosum, int maxWords) {
        File sourceFile = new File(source);
        String[] directories = sourceFile.list();
        File auto = new File(autosum);
        if (!auto.exists()) {
            auto.mkdir();
        }
        int counter = 0;

        for (String d : directories) {
            File directory = new File(source + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (File file : files) {
                File auto_sub = new File(autosum + "/" + d);
                if (!auto_sub.exists()) {
                    auto_sub.mkdir();
                }
                sum.summarize(file.getPath(), autosum + "/" + d + "/" + file.getName(), maxWords);
                counter++;
            }
        }
        System.out.println("\n" + counter + " văn bản đã được tóm tắt");
    }

    private double[][] evalFolder(String autosum, String sum) {
        File dir = new File(autosum);
        String[] directories = dir.list();
        int counter = 0;
        double[][] avg = new double[4][3];
        String output = "";
        for (String d : directories) {
            File directory = new File(dir.getPath() + "/" + d);
            if (directory.isFile()) {
                continue;
            }
            File[] files = directory.listFiles();    // Reading directory contents
            for (File file : files) {
                try {
                    String name = file.getName();
                    double[][] rouge = eval.rouge(autosum + "/" + d + "/" + name,
                            new String[]{sum + "/" + d + "/" + name});
                    output += d + "/" + name + ":\n";
                    for (int i = 0; i < 4; i++) {
                        double[] r = rouge[i];
                        for (int j = 0; j < r.length; j++) {
                            avg[i][j] += r[j];
                        }
//                        System.out.println("Recall = " + r[0] + "\tPrecision = " + r[1] + "\tF = " + r[2]);
                        output += "Rouge-" + (i + 1) + ": \tRecall = " + r[0] + "\tPrecision = " + r[1] + "\tF = " + r[2] + "\n";
                    }
                    output += "\n";
                    counter++;
                }catch (Exception ex) {
//                    System.out.println("Failure on file " + files[k].getPath() + "!\n\n");
                }
            }
        }
        System.out.println("\n" + counter + " văn bản đã được đánh giá");

        output += "Average:\n";
        for (int i = 0; i < 4; i++) {
            avg[i][0] /= counter;
            avg[i][1] /= counter;
            avg[i][2] /= counter;
            output += "Rouge-" + (i + 1) + ": \tRecall = " + avg[i][0] + "\tPrecision = " + avg[i][1] + "\tF = " + avg[i][2] + "\n";
        }
        IOUtil.WriteToFile("temp/my_eval_ui.txt", output);
        JOptionPane.showMessageDialog(this, "Kết quả chi tiết đã được lưu vào file my_eval_ui.txt trong thư mục temp");
//        System.out.println(output);

        return avg;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        lbSource = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        lbAutoSum = new javax.swing.JLabel();
        txtAutoSum = new javax.swing.JTextField();
        lbSum = new javax.swing.JLabel();
        txtSum = new javax.swing.JTextField();
        chbAutoSumExist = new javax.swing.JCheckBox();
        lbEval = new javax.swing.JLabel();
        lbEval1 = new javax.swing.JLabel();
        lbEval2 = new javax.swing.JLabel();
        lbEval3 = new javax.swing.JLabel();
        lbEval4 = new javax.swing.JLabel();
        btnEval = new javax.swing.JButton();
        btnSource = new javax.swing.JButton();
        btnAutoSum = new javax.swing.JButton();
        btnSum = new javax.swing.JButton();

        jButton3.setText("File");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Đánh giá tóm tắt văn bản");

        lbSource.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbSource.setText("Nhập đường dẫn văn bản gốc hoặc thư mục văn bản gốc:");

        txtSource.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        lbAutoSum.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbAutoSum.setText("Nhập đường dẫn văn bản tóm tắt hoặc thư mục văn bản tóm tắt:");

        txtAutoSum.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        lbSum.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbSum.setText("Nhập đường dẫn văn bản tham chiếu hoặc thư mục văn bản tham chiếu:");

        txtSum.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        chbAutoSumExist.setText("Đã có văn bản tóm tắt");
        chbAutoSumExist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbAutoSumExistActionPerformed(evt);
            }
        });

        lbEval.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbEval.setText("Kết quả đánh giá ROUGE-N (Recall - Precision - F-score):");

        lbEval1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbEval1.setText("ROUGE-1:");

        lbEval2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbEval2.setText("ROUGE-2:");

        lbEval3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbEval3.setText("ROUGE-3:");

        lbEval4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lbEval4.setText("ROUGE-4:");

        btnEval.setText("Đánh giá");
        btnEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEvalActionPerformed(evt);
            }
        });

        btnSource.setText("File");
        btnSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSourceActionPerformed(evt);
            }
        });

        btnAutoSum.setText("File");
        btnAutoSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoSumActionPerformed(evt);
            }
        });

        btnSum.setText("File");
        btnSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSumActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbEval1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbEval2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbEval3, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addComponent(lbEval4, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSum, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAutoSum, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSource, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chbAutoSumExist, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbSource, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbAutoSum, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbSum, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEval, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbEval, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btnSource, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSum, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAutoSum, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbSource)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSource))
                .addGap(26, 26, 26)
                .addComponent(chbAutoSumExist)
                .addGap(18, 18, 18)
                .addComponent(lbAutoSum)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtAutoSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAutoSum))
                .addGap(31, 31, 31)
                .addComponent(lbSum)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSum))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(btnEval)
                .addGap(18, 18, 18)
                .addComponent(lbEval)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbEval1)
                    .addComponent(lbEval2))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbEval3)
                    .addComponent(lbEval4))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chbAutoSumExistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbAutoSumExistActionPerformed
        boolean existSum = chbAutoSumExist.isSelected();
        txtAutoSum.setEnabled(existSum);
        lbAutoSum.setEnabled(existSum);
        btnAutoSum.setEnabled(existSum);
        txtSource.setEnabled(!existSum);
        lbSource.setEnabled(!existSum);
        btnSource.setEnabled(!existSum);
    }//GEN-LAST:event_chbAutoSumExistActionPerformed

    private void btnEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEvalActionPerformed
        String sourcePath = txtSource.getText();
        String autoSumPath = txtAutoSum.getText();
        String sumPath = txtSum.getText();
        boolean isFile = sumPath.contains(".");
        double[][] rouge;
        if (isFile) {
            if (chbAutoSumExist.isSelected()) {
                rouge = eval.rouge(autoSumPath, new String[]{sumPath});
            } else {
                autoSumPath = "temp/1.temp";
                sum.summarize(sourcePath, autoSumPath, 120);
                rouge = eval.rouge(autoSumPath, new String[]{sumPath});
                IOUtil.DeleteFile(autoSumPath);
            }
        } else {
            if (chbAutoSumExist.isSelected()) {
                rouge = evalFolder(autoSumPath, sumPath);
            } else {
                autoSumPath = "temp/AutoSummary";
                summarizeFolder(sourcePath, autoSumPath, 120);
                rouge = evalFolder(autoSumPath, sumPath);
                IOUtil.DeleteFolder(sourcePath);
            }
        }
        lbEval1.setText("ROUGE-1:  " + decimalFormat.format(rouge[0][0]) + " - "
                + decimalFormat.format(rouge[0][1]) + " - " + decimalFormat.format(rouge[0][2]));
        lbEval2.setText("ROUGE-2:  " + decimalFormat.format(rouge[1][0]) + " - "
                + decimalFormat.format(rouge[1][1]) + " - " + decimalFormat.format(rouge[1][2]));
        lbEval3.setText("ROUGE-3:  " + decimalFormat.format(rouge[2][0]) + " - "
                + decimalFormat.format(rouge[2][1]) + " - " + decimalFormat.format(rouge[2][2]));
        lbEval4.setText("ROUGE-4:  " + decimalFormat.format(rouge[3][0]) + " - "
                + decimalFormat.format(rouge[3][1]) + " - " + decimalFormat.format(rouge[3][2]));
    }//GEN-LAST:event_btnEvalActionPerformed

    private void btnSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSourceActionPerformed
        int returnVal = fc.showOpenDialog(EvalUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtSource.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnSourceActionPerformed

    private void btnAutoSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoSumActionPerformed
        int returnVal = fc.showOpenDialog(EvalUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtAutoSum.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnAutoSumActionPerformed

    private void btnSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSumActionPerformed
        int returnVal = fc.showOpenDialog(EvalUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtSum.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnSumActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EvalUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EvalUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAutoSum;
    private javax.swing.JButton btnEval;
    private javax.swing.JButton btnSource;
    private javax.swing.JButton btnSum;
    private javax.swing.JCheckBox chbAutoSumExist;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel lbAutoSum;
    private javax.swing.JLabel lbEval;
    private javax.swing.JLabel lbEval1;
    private javax.swing.JLabel lbEval2;
    private javax.swing.JLabel lbEval3;
    private javax.swing.JLabel lbEval4;
    private javax.swing.JLabel lbSource;
    private javax.swing.JLabel lbSum;
    private javax.swing.JTextField txtAutoSum;
    private javax.swing.JTextField txtSource;
    private javax.swing.JTextField txtSum;
    // End of variables declaration//GEN-END:variables
}