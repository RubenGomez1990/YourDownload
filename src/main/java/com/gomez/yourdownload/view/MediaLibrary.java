package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller;
import com.gomez.yourdownload.model.DownloadInfoTableModel;
import com.gomez.yourdownload.model.DownloadInfo;
import com.gomez.model.Media;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author LionKeriot
 */
public class MediaLibrary extends javax.swing.JPanel {

    private final MainScreen principal;
    private final JPanel originalPanel;
    private final List<DownloadInfo> resourcesList;
    private DownloadInfoTableModel tableModel;
    private final MediaPoller mediaPoller;

    public MediaLibrary(MainScreen principal, JPanel originalPanel, List<DownloadInfo> resourcesList, MediaPoller mediaPoller) {
        this.principal = principal;
        this.originalPanel = originalPanel;
        this.resourcesList = resourcesList;
        this.mediaPoller = mediaPoller;

        initComponents();
        tableModel = new DownloadInfoTableModel(resourcesList);
        jTableMedia.setModel(tableModel);

        javax.swing.table.TableRowSorter<DownloadInfoTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        jTableMedia.setRowSorter(sorter);

        initFiltroComboBox();
    }

    private void loadAllMediaInfo() {
        // Lanzamos la operación de red en un hilo secundario para no bloquear la UI
        new Thread(() -> {
            // Verificación de seguridad
            if (this.mediaPoller == null) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(principal,
                        "Error: MediaPoller instance is missing. Cannot sync.", "Fatal Error", JOptionPane.ERROR_MESSAGE));
                return;
            }

            try {
                // 1. OBTENER ARCHIVOS DE LA RED DI MEDIA
                List<Media> networkFiles = this.mediaPoller.getAllMedia();

                // 2. CREAR UN MAPA para buscar rápidamente los archivos de la red por su nombre
                Map<String, Media> networkMap = new HashMap<>();
                for (Media media : networkFiles) {
                    // 🛑 CORRECCIÓN: Usamos el campo directo del modelo del componente
                    networkMap.put(media.mediaFileName.toLowerCase(), media);
                }

                // 3. FUSIONAR LISTAS: Iterar sobre la lista LOCAL (resourcesList)
                for (DownloadInfo localInfo : this.resourcesList) {
                    // 💥 ATENCIÓN: Usamos java.io.File para no depender de import incorrecto
                    if (localInfo.getAbsolutePath() != null && new File(localInfo.getAbsolutePath()).exists()) {
                        String localFileName = localInfo.getFileName().toLowerCase();

                        if (networkMap.containsKey(localFileName)) {
                            // Estado: AMBOS (Local y en Red)
                            Media networkMedia = networkMap.get(localFileName);

                            // 🛑 CORRECCIÓN: Usamos los campos directos para la asignación
                            localInfo.setNetworkId(networkMedia.id);
                            localInfo.setIsInNetwork(true);
                            localInfo.setIsNetworkOnly(false);

                            networkMap.remove(localFileName);
                        } else {
                            // Estado: SOLO LOCAL (Limpieza de estado de red)
                            localInfo.setIsInNetwork(false);
                            localInfo.setIsNetworkOnly(false);
                            localInfo.setNetworkId(null);
                        }
                    } else {
                        // Archivo huérfano (historial pero no disco)
                        localInfo.setIsInNetwork(false);
                        localInfo.setIsNetworkOnly(false);
                        localInfo.setNetworkId(null);
                        // No lo eliminamos de resourcesList para mantener el historial.
                    }
                }

                for (Media networkMedia : networkMap.values()) {
                    // Estado: SOLO RE
                    // 🛑 CORRECCIÓN: Casteo de long primitiva (Media) a Long objeto (DownloadInfo)
                    Long fileSize = Long.valueOf(networkMedia.mediaFileSize);

                    DownloadInfo networkOnlyInfo = new DownloadInfo(
                            networkMedia.id,
                            networkMedia.mediaFileName,
                            fileSize,
                            networkMedia.mediaMimeType
                    );
                    this.resourcesList.add(networkOnlyInfo);
                }

                // 5. TERMINADO: Actualizar la UI
                SwingUtilities.invokeLater(() -> {
                    this.tableModel.fireTableDataChanged();
                    JOptionPane.showMessageDialog(principal,
                            "Library updated! Found " + networkFiles.size() + " total network files.",
                            "Sync Complete", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(principal,
                            "Error loading network media. Check token/connection: " + e.getMessage(),
                            "API Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println("API Error in MediaLibrary: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneMedia = new javax.swing.JScrollPane();
        jTableMedia = new javax.swing.JTable();
        jLabelFilter = new javax.swing.JLabel();
        jComboBoxFilter = new javax.swing.JComboBox<>();
        jButtonDelete = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();
        jButtonUpload1 = new javax.swing.JButton();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(937, 371));
        setLayout(null);

        jTableMedia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneMedia.setViewportView(jTableMedia);

        add(jScrollPaneMedia);
        jScrollPaneMedia.setBounds(0, 0, 930, 220);

        jLabelFilter.setText("Filter by:");
        add(jLabelFilter);
        jLabelFilter.setBounds(10, 230, 70, 30);

        jComboBoxFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterActionPerformed(evt);
            }
        });
        add(jComboBoxFilter);
        jComboBoxFilter.setBounds(130, 230, 120, 30);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        add(jButtonDelete);
        jButtonDelete.setBounds(400, 230, 120, 30);

        jButtonBack.setText("Return");
        jButtonBack.setName(""); // NOI18N
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });
        add(jButtonBack);
        jButtonBack.setBounds(690, 230, 120, 30);

        jButtonSearch.setText("Search by text");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        add(jButtonSearch);
        jButtonSearch.setBounds(130, 280, 120, 30);

        jButtonDownload.setText("Download");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });
        add(jButtonDownload);
        jButtonDownload.setBounds(690, 280, 120, 30);

        jButtonUpload1.setText("Upload");
        jButtonUpload1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpload1ActionPerformed(evt);
            }
        });
        add(jButtonUpload1);
        jButtonUpload1.setBounds(400, 280, 120, 30);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        principal.setContentPane(originalPanel);
        principal.revalidate();
        principal.repaint();
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        // Obtiene la fila seleccionada
        Object[] options = {"Yes", "No"};
        int selectedRow = jTableMedia.getSelectedRow();

        // Asignamos una variable al recurso que vamos a eliminar.
        if (selectedRow >= 0) {
            DownloadInfo resourceDelete = resourcesList.get(selectedRow);

            // Pedimos confirmación
            int confirmation = javax.swing.JOptionPane.showOptionDialog(this, "Are you sure you want to delete '"
                    + resourceDelete.getFileName(), "Confirm Deletion",
                    javax.swing.JOptionPane.YES_NO_OPTION, // Tipo de opción
                    javax.swing.JOptionPane.QUESTION_MESSAGE, // Icono de pregunta
                    null, // No usar icono custom
                    options, // Array de Strings a mostrar en los botones
                    options[0] // Opción por defecto ("Yes")
            );

            if (confirmation == javax.swing.JOptionPane.YES_OPTION) {
                java.io.File file = new java.io.File(resourceDelete.getAbsolutePath());

                if (file.delete()) {
                    resourcesList.remove(selectedRow);
                    tableModel.fireTableDataChanged();
                    javax.swing.JOptionPane.showMessageDialog(this, "File deleted successfully.", "Success!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Error al eliminar del disco (ej: el archivo no existe o permisos insuficientes)
                    javax.swing.JOptionPane.showMessageDialog(this, "Error: File could not have been deleted. Check the log.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Select a file to delete", "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jComboBoxFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFilterActionPerformed

        @SuppressWarnings("unchecked")
        javax.swing.table.TableRowSorter<DownloadInfoTableModel> sorter
                = (javax.swing.table.TableRowSorter<DownloadInfoTableModel>) jTableMedia.getRowSorter();

        if (sorter == null) {
            return;
        }
        String selectedFilter = (String) jComboBoxFilter.getSelectedItem();

        if (selectedFilter.equals("All Types")) {
            // CASO A: Mostrar todas las filas.
            sorter.setRowFilter(null);
        } else {
            // CASO B: Filtrar las filas (el caso que antes estaba en el 'else').
            String mimeTypeFilter = "";
            // Lógica de traducción
            if (selectedFilter.contains("MP4")) {
                mimeTypeFilter = "video/mp4";
            } else if (selectedFilter.contains("AVI")) {
                mimeTypeFilter = "video/x-msvideo";
            } else if (selectedFilter.contains("MP3")) {
                mimeTypeFilter = "audio/mpeg";
            }

            // Aplicar el filtro a la Columna 2 (MIME Type)
            javax.swing.RowFilter<Object, Object> rf
                    = javax.swing.RowFilter.regexFilter("^" + mimeTypeFilter + "$", 2);

            sorter.setRowFilter(rf);
        }
    }//GEN-LAST:event_jComboBoxFilterActionPerformed

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        javax.swing.JFrame parentFrame = principal;

        try {
            // La ejecución crítica que puede fallar:
            SearchDialog search = new SearchDialog(parentFrame, resourcesList);
            search.setVisible(true);

        } catch (Exception e) {
            // Capturamos la excepción (NPE, error de componente, etc.)
            javax.swing.JOptionPane.showMessageDialog(principal,
                    "Error opening search: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);

            // Esta línea es crucial para ver la traza de error completa en la consola
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        int selectedRow = jTableMedia.getSelectedRow();

        if (selectedRow >= 0) {
            int modelRow = jTableMedia.convertRowIndexToModel(selectedRow);
            DownloadInfo fileToDownload = resourcesList.get(modelRow);

            if (!fileToDownload.isNetworkOnly() || fileToDownload.getNetworkId() == null) {
                JOptionPane.showMessageDialog(this, "The selected file is not available on the network or already downloaded.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.io.File downloadFolder = new java.io.File("downloads");
            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs(); // Crea la carpeta si no existe
            }
            
            java.io.File destinationFile = new java.io.File(downloadFolder, fileToDownload.getFileName());

            if (destinationFile.exists()) {
                JOptionPane.showMessageDialog(this, "A file with that name already exists in the download folder.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Thread(() -> {
                try {
                    this.mediaPoller.download(fileToDownload.getNetworkId(), destinationFile);
                    DownloadInfo newLocalInfo = new DownloadInfo(
                            destinationFile.getAbsolutePath(),
                            new java.util.Date(), // Fecha de descarga actual
                            destinationFile.length(),
                            fileToDownload.getMimeType()
                    );
                    newLocalInfo.setNetworkId(fileToDownload.getNetworkId());
                    newLocalInfo.setIsInNetwork(true);
                    newLocalInfo.setIsNetworkOnly(false);

                    SwingUtilities.invokeLater(() -> {
                        resourcesList.remove(modelRow);

                        resourcesList.add(newLocalInfo);

                        JOptionPane.showMessageDialog(this, "Download successful and file saved to: " + destinationFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);

                        this.tableModel.fireTableDataChanged();
                    });

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Download failed for " + fileToDownload.getFileName() + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.err.println("Download Error: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Select a file to download.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDownloadActionPerformed

    private void jButtonUpload1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpload1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonUpload1ActionPerformed

    private void initFiltroComboBox() {
        // 1. Definimos las opciones de filtro con nombres amigables para el usuario
        String[] filterTypes = {
            "All Types", // Opción 0: Desactivar filtro
            "Video (MP4)", // Opción para buscar "video/mp4"
            "Audio (MP3)", // Opción para buscar "audio/mpeg"
            "Video (AVI)" // Opción para buscar "video/x-msvideo"
        };

        // 2. Asignamos las opciones al JComboBox
        javax.swing.DefaultComboBoxModel<String> filterModel = new javax.swing.DefaultComboBoxModel<>(filterTypes);
        jComboBoxFilter.setModel(filterModel);

        // 3. Conectamos el JComboBox a la lógica de acción
        jComboBoxFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Llama al método que contiene la lógica del RowFilter
                jComboBoxFilterActionPerformed(evt);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonUpload1;
    private javax.swing.JComboBox<String> jComboBoxFilter;
    private javax.swing.JLabel jLabelFilter;
    private javax.swing.JScrollPane jScrollPaneMedia;
    private javax.swing.JTable jTableMedia;
    // End of variables declaration//GEN-END:variables
}
