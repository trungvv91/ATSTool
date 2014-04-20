/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestFrame.java
 *
 * Created on Aug 31, 2013, 2:02:21 PM
 */
package nlp.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import nlp.graph.WordsGraph;
import nlp.sentenceExtraction.Datum;
import nlp.sentenceExtraction.MyTagger;

/**
 *
 * @author Trung
 */
public class TestFrame extends javax.swing.JFrame {

    public String sourceText;
    public String sumText;
    
    /**
     * @param source
     * @param wordMax
     * @return
     * @throws java.io.IOException
     */
    public static String summarize(String source, int wordMax) throws IOException {
//        System.out.println(source);
        String displayFile = "corpus/Plaintext/displayFile.txt";
        String inputNum = "displayFile";
        try (FileWriter fr = new FileWriter(new File(displayFile))) {
            fr.write(source);
        }
        WordsGraph graph = new WordsGraph();
        MyTagger tagger = new MyTagger();
        ArrayList<Datum> data = tagger.getData(inputNum);
        try {
            graph.mainWordGraph(inputNum, data, wordMax);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        String out = graph.outString;
//        System.out.println(out);
        return out;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public void setSumText(String sumText) {
        this.sumText = sumText;
    }

    public String getSourceText() {
        return sourceText;
    }

    public String getSumText() {
        return sumText;
    }

    /**
     * Creates new form TestFrame
     */
    public TestFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orginalDocumentLable = new javax.swing.JLabel();
        summaryDocumentLable = new javax.swing.JLabel();
        summaryButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourceTextArea = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        sumTextArea = new javax.swing.JTextPane();
        sourceNumOfWords = new javax.swing.JTextField();
        sumNumOfWords = new javax.swing.JTextField();
        orginalDocumentLable1 = new javax.swing.JLabel();
        orginalDocumentLable2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        orginalDocumentLable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        orginalDocumentLable.setText("Văn bản ban đầu");

        summaryDocumentLable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        summaryDocumentLable.setText("Văn bản tóm tắt");

        summaryButton.setText("Summary");
        summaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summaryButtonActionPerformed(evt);
            }
        });

        sourceTextArea.setMinimumSize(new java.awt.Dimension(450, 550));
        jScrollPane1.setViewportView(sourceTextArea);
        sourceTextArea.getAccessibleContext().setAccessibleName("");
        sourceTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                String str = sourceTextArea.getText();
                String[] words = str.split("\\s+");
                sourceNumOfWords.setText(String.valueOf(words.length));
            }
        });

        sumTextArea.setMinimumSize(new java.awt.Dimension(450, 550));
        jScrollPane2.setViewportView(sumTextArea);

        sourceNumOfWords.setEnabled(false);

        sumNumOfWords.setText("120");

        orginalDocumentLable1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        orginalDocumentLable1.setText("từ");

        orginalDocumentLable2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        orginalDocumentLable2.setText("từ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(orginalDocumentLable, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceNumOfWords, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orginalDocumentLable1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(summaryButton)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(summaryDocumentLable, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sumNumOfWords, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orginalDocumentLable2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(176, 176, 176))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sourceNumOfWords, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(orginalDocumentLable)
                        .addComponent(summaryDocumentLable)
                        .addComponent(sumNumOfWords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(orginalDocumentLable1)
                        .addComponent(orginalDocumentLable2)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryButton)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void summaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summaryButtonActionPerformed
        if (sourceTextArea.getText() != null) {
            String out = "";
            sumTextArea.setText(null);
            int wordMax = Integer.parseInt(sumNumOfWords.getText());
            try {
                out = summarize(sourceTextArea.getText(), wordMax);
            } catch (IOException ex) {
                Logger.getLogger(TestFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            sumTextArea.setText(out);
        }
    }//GEN-LAST:event_summaryButtonActionPerformed

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
                if ("Window".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TestFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel orginalDocumentLable;
    private javax.swing.JLabel orginalDocumentLable1;
    private javax.swing.JLabel orginalDocumentLable2;
    private javax.swing.JTextField sourceNumOfWords;
    private javax.swing.JTextPane sourceTextArea;
    private javax.swing.JTextField sumNumOfWords;
    private javax.swing.JTextPane sumTextArea;
    private javax.swing.JButton summaryButton;
    private javax.swing.JLabel summaryDocumentLable;
    // End of variables declaration//GEN-END:variables
}