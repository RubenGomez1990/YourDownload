package com.gomez.yourdownload.view;


import com.gomez.yourdownload.model.DownloadInfo;
import com.gomez.yourdownload.service.DownloadService;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialog window for searching and managing downloaded media files.
 * Provides real-time filtering of the resources list and options to open or delete files.
 * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class SearchDialog extends javax.swing.JDialog {
    
    /** 
     * Logger for tracking service-level events and errors. 
     */
    public static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SearchDialog.class.getName());
    private final List<DownloadInfo> resourcesList;
    
    /**
     * Creates a new SearchDialog instance.
     * @param parent The parent Frame that owns this dialog.
     * @param resourcesList The list of files available for searching.
     */
    public SearchDialog(java.awt.Frame parent, List<DownloadInfo> resourcesList) {
        super(parent, true);
        this.resourcesList = resourcesList;
        initComponents();
 
        javax.swing.SwingUtilities.invokeLater(() -> {
        // Estas líneas ahora se ejecutarán después de que la ventana esté lista.
        initSearching();
        applySearchingFilter(""); 
    });
        
        setTitle("Search Files");
        setSize(1024,768);
        setLocationRelativeTo(parent);
    }
    

        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldIntroduce = new javax.swing.JTextField();
        jScrollPaneList = new javax.swing.JScrollPane();
        jListSearchList = new javax.swing.JList<>();
        jButtonPlay = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jTextFieldIntroduce.setText("Enter a word");
        jTextFieldIntroduce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIntroduceActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldIntroduce);
        jTextFieldIntroduce.setBounds(30, 16, 279, 30);

        jListSearchList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPaneList.setViewportView(jListSearchList);

        getContentPane().add(jScrollPaneList);
        jScrollPaneList.setBounds(30, 81, 450, 146);

        jButtonPlay.setText("Play it!");
        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlayActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPlay);
        jButtonPlay.setBounds(30, 250, 72, 23);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(120, 250, 72, 23);

        jButtonReturn.setText("Return");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(210, 250, 72, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Attempts to open the selected file using the system's default media player.
     * @param evt The action event triggered by the play button.
     */
    private void jButtonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlayActionPerformed
        Object selectedValue = jListSearchList.getSelectedValue();
        DownloadInfo resource = (DownloadInfo) selectedValue; // Cast explícito

    if (resource != null){
        java.io.File file = new java.io.File(resource.getAbsolutePath());
        
        if (file.exists()){
            // [Si el archivo existe, el try-catch de apertura es correcto]
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (java.io.IOException e){
                JOptionPane.showMessageDialog(this, "Error opening the file: " + e.getMessage(), "I/O error", JOptionPane.ERROR_MESSAGE);
                logger.log(java.util.logging.Level.SEVERE, "Error opening the file.", e);
            }
        } else {
            // --- CÓDIGO CORREGIDO: Muestra la ruta estática que no existe ---
            JOptionPane.showMessageDialog(this, 
                "File not found at the stored location:\n" + resource.getAbsolutePath(), 
                "Error: File Missing", 
                JOptionPane.ERROR_MESSAGE);
            // -------------------------------------------------------------------
        }
    } else {
        JOptionPane.showMessageDialog(this, "First, select a valid file.");
    }
        
    }//GEN-LAST:event_jButtonPlayActionPerformed
    /**
     * Deletes the selected file from the local disk and removes it from the shared resource list.
     * @param evt The action event triggered by the delete button.
     */
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
    
    Object selectedValue = jListSearchList.getSelectedValue();
    DownloadInfo resourceToDelete = (DownloadInfo) selectedValue;

        if (resourceToDelete == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "First select a file", "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
    
    Object[] options = {"Yes", "No"};
    int confirmationResult = javax.swing.JOptionPane.showOptionDialog(this,
        "Are you sure you want to delete '" + resourceToDelete.getFileName() + "'?\nThis action will delete the file from the disk.",
        "Confirm Deletion",
        javax.swing.JOptionPane.YES_NO_OPTION,
        javax.swing.JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]
    );

    if (confirmationResult == YES_OPTION) {
        java.io.File file = new java.io.File(resourceToDelete.getAbsolutePath());
            if (file.delete()) {
                resourcesList.remove(resourceToDelete);
                DownloadService.saveHistory(resourcesList);
                applySearchingFilter(jTextFieldIntroduce.getText());
                javax.swing.JOptionPane.showMessageDialog(this, "File deleted successfully.", "Success!", javax.swing.JOptionPane.INFORMATION_MESSAGE); 
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Error: File could not be deleted. Check if the file is currently open.", 
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    
   
    }//GEN-LAST:event_jButtonDeleteActionPerformed
    /**
     * Handles the action event for the jTextFieldIntroduceAction.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jTextFieldIntroduceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIntroduceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIntroduceActionPerformed
    /**
     * Closes the dialog window.
     * @param evt The action event triggered by the return button.
     */
    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed
    /**
     * Configures the document listener for the search field to provide real-time results.
     */
    public void initSearching() {
        jTextFieldIntroduce.getDocument().addDocumentListener(new DocumentListener(){
           
            public void handleUpdate(){
                String text = jTextFieldIntroduce.getText();
                applySearchingFilter(text);
            }
            
            @Override
            public void insertUpdate(DocumentEvent e){
                handleUpdate();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e){
                handleUpdate();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e){
            }
        });
    }
    /**
     * Filters the resources list based on the user's input and updates the JList display.
     * @param searchingText The text string to search for in the filenames.
     */
    public void applySearchingFilter(String searchingText) {
    
        String lowerSearchingText = searchingText.toLowerCase();
        DefaultListModel<DownloadInfo> resultsModel = new DefaultListModel<>();

        if (searchingText.isEmpty()) {
            for (DownloadInfo resource : resourcesList) {
                resultsModel.addElement(resource);
            }
        } else {
            for (DownloadInfo resource : resourcesList) {

                String lowerArchiveName = resource.getFileName().toLowerCase();

                if (lowerArchiveName.contains(lowerSearchingText)) {
                    resultsModel.addElement(resource);
                }
            }
        }
    @SuppressWarnings("unchecked")
        javax.swing.ListModel rawModel = (javax.swing.ListModel) resultsModel;
        jListSearchList.setModel(rawModel);
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JList<String> jListSearchList;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JTextField jTextFieldIntroduce;
    // End of variables declaration//GEN-END:variables
}
