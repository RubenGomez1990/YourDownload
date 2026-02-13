package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller;
import com.gomez.yourdownload.model.DownloadInfoTableModel;
import com.gomez.yourdownload.model.DownloadInfo;
import com.gomez.model.Media;
import java.io.File;
import java.util.ArrayList;
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

    private final MainScreen mainScreen;
    private final JPanel originalPanel;
    private final List<DownloadInfo> resourcesList;
    private DownloadInfoTableModel tableModel;
    private final MediaPoller mediaPoller;

    public MediaLibrary(MainScreen principal, JPanel originalPanel, List<DownloadInfo> resourcesList, MediaPoller mediaPoller) {
        this.mainScreen = principal;
        this.originalPanel = originalPanel;
        this.resourcesList = resourcesList;
        this.mediaPoller = mediaPoller;

        initComponents();

        // 1. Inicializamos el modelo
        tableModel = new DownloadInfoTableModel(resourcesList);
        jTableMedia.setModel(tableModel);

        // 2. CREACIÓN MANUAL DEL SORTER (Para que no sea null)
        javax.swing.table.TableRowSorter<DownloadInfoTableModel> sorter
                = new javax.swing.table.TableRowSorter<>(tableModel);
        jTableMedia.setRowSorter(sorter);
        jTableMedia.getColumnModel().getColumn(0).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText((value == null) ? "N/A" : value.toString());
            }
        });

        // 3. CONFIGURACIÓN DEL ORDEN INICIAL POR ID (Columna 0)
        java.util.List<javax.swing.RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        // 4. Inits de filtros y carga
        initFiltroComboBox();
        loadAllMediaInfo();
    }

    private void loadAllMediaInfo() {
        new Thread(() -> {
            if (this.mediaPoller == null) {
                return;
            }

            try {
                List<com.gomez.model.Media> networkFiles = this.mediaPoller.getAllMedia();
                if (networkFiles == null) {
                    return;
                }

                // 1. Mapa de red (Normalizamos el nombre para evitar fallos por mayúsculas/espacios)
                Map<String, com.gomez.model.Media> networkMap = new HashMap<>();
                for (com.gomez.model.Media m : networkFiles) {
                    if (m.mediaFileName != null) {
                        networkMap.put(m.mediaFileName.trim().toLowerCase(), m);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    // 2. Usamos un Set para no añadir dos veces lo mismo por error
                    java.util.Set<String> processedNames = new java.util.HashSet<>();
                    java.util.List<DownloadInfo> mergedList = new ArrayList<>();

                    // 3. PASO 1: Revisar lo que ya tenemos en local/historial
                    for (DownloadInfo local : resourcesList) {
                        String cleanName = local.getFileName().trim().toLowerCase();

                        if (networkMap.containsKey(cleanName)) {
                            // CASO: ESTÁ EN AMBOS (Synced)
                            com.gomez.model.Media net = networkMap.get(cleanName);
                            local.setNetworkId(net.id);
                            local.setIsInNetwork(true);
                            local.setIsNetworkOnly(false);
                            networkMap.remove(cleanName); // Lo quitamos del mapa de red
                        } else {
                            // CASO: SOLO LOCAL
                            local.setIsInNetwork(false);
                            local.setNetworkId(null);
                        }

                        if (!processedNames.contains(cleanName)) {
                            mergedList.add(local);
                            processedNames.add(cleanName);
                        }
                    }

                    // 4. PASO 2: Añadir lo que queda en la red que NO teníamos localmente
                    for (com.gomez.model.Media net : networkMap.values()) {
                        String cleanName = net.mediaFileName.trim().toLowerCase();
                        if (!processedNames.contains(cleanName)) {
                            DownloadInfo cloudOnly = new DownloadInfo(
                                    net.id, net.mediaFileName, (long) net.mediaFileSize, net.mediaMimeType
                            );
                            mergedList.add(cloudOnly);
                            processedNames.add(cleanName);
                        }
                    }

                    // 5. ACTUALIZACIÓN FINAL: Sustituimos la lista vieja por la nueva limpia
                    resourcesList.clear();
                    resourcesList.addAll(mergedList);

                    tableModel.fireTableDataChanged();

                    // Re-ordenar por ID tras la limpieza
                    if (jTableMedia.getRowSorter() instanceof javax.swing.DefaultRowSorter) {
                        ((javax.swing.DefaultRowSorter<?, ?>) jTableMedia.getRowSorter()).sort();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
        mainScreen.setContentPane(originalPanel);
        mainScreen.setSize(1024, 330); // Reajuste de tamaño al volver
        mainScreen.revalidate();
        mainScreen.repaint();
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        // 1. Get the selected row from the view
        int selectedRow = jTableMedia.getSelectedRow();
        Object[] options = {"Yes", "No"};

        if (selectedRow >= 0) {
            // 2. CRITICAL: Convert the view index to model index (in case of sorting)
            int modelRow = jTableMedia.convertRowIndexToModel(selectedRow);
            DownloadInfo resourceDelete = resourcesList.get(modelRow);

            // 3. Request confirmation
            int confirmation = javax.swing.JOptionPane.showOptionDialog(this,
                    "Are you sure you want to delete '" + resourceDelete.getFileName() + "'?",
                    "Confirm Deletion",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (confirmation == javax.swing.JOptionPane.YES_OPTION) {
                String path = resourceDelete.getAbsolutePath();
                boolean deletedFromDisk = true; // Default true for cloud-only files

                // 4. Check if there is a local file to delete
                if (path != null && !path.isEmpty()) {
                    java.io.File file = new java.io.File(path);
                    if (file.exists()) {
                        deletedFromDisk = file.delete();
                    }
                }

                if (deletedFromDisk) {
                    // 5. Remove from list and update table
                    resourcesList.remove(modelRow);
                    tableModel.fireTableDataChanged();

                    // 6. Persist changes to history JSON
                    com.gomez.yourdownload.service.DownloadService.saveHistory(resourcesList);

                    javax.swing.JOptionPane.showMessageDialog(this, "Entry removed successfully.", "Success!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error: Local file could not be deleted.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Please, select a file to delete", "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
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

        if (!selectedFilter.equals("All Types")) {
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
                    = javax.swing.RowFilter.regexFilter("^" + mimeTypeFilter + "$", 3);
            sorter.setRowFilter(rf);
        }
    }//GEN-LAST:event_jComboBoxFilterActionPerformed

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        javax.swing.JFrame parentFrame = mainScreen;

        try {
            // La ejecución crítica que puede fallar:
            SearchDialog search = new SearchDialog(parentFrame, resourcesList);
            search.setVisible(true);

        } catch (Exception e) {
            // Capturamos la excepción (NPE, error de componente, etc.)
            javax.swing.JOptionPane.showMessageDialog(mainScreen,
                    "Error opening search: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);

            // Esta línea es crucial para ver la traza de error completa en la consola
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
// 1. Obtener la fila seleccionada de la vista
        int selectedRow = jTableMedia.getSelectedRow();

        if (selectedRow >= 0) {
            // 2. CRÍTICO: Convertir el índice visual al índice del modelo para que funcione al ordenar
            int modelRow = jTableMedia.convertRowIndexToModel(selectedRow);
            DownloadInfo resource = resourcesList.get(modelRow);

            // 3. VALIDACIÓN MEJORADA: ¿Tiene ID de red y NO tiene ruta local?
            // Esto permite descargar cualquier archivo que esté en la nube pero no en tu PC.
            if (resource.getNetworkId() == null || resource.getAbsolutePath() != null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "The selected file is not available on the network or is already downloaded.",
                        "Download Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Preparar carpeta de descargas (Minimalista: usa la ruta configurada o la carpeta 'downloads')
            java.io.File downloadFolder = new java.io.File("downloads");
            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs();
            }

            java.io.File destinationFile = new java.io.File(downloadFolder, resource.getFileName());

            // 5. Evitar sobreescribir si el archivo ya existe físicamente
            if (destinationFile.exists()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "A file with that name already exists in the download folder.",
                        "File Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 6. Hilo secundario para no congelar la interfaz
            new Thread(() -> {
                try {
                    // Descarga real a través del componente Poller
                    this.mediaPoller.download(resource.getNetworkId(), destinationFile);

                    // Crear el nuevo objeto de información local (Synced)
                    DownloadInfo syncInfo = new DownloadInfo(
                            destinationFile.getAbsolutePath(),
                            new java.util.Date(),
                            destinationFile.length(),
                            resource.getMimeType()
                    );
                    syncInfo.setNetworkId(resource.getNetworkId());
                    syncInfo.setIsInNetwork(true);
                    syncInfo.setIsNetworkOnly(false); // Ya no es solo red, ahora es local también

                    // Actualizar la interfaz en el hilo de Swing
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Reemplazamos el registro de "solo nube" por el nuevo "sincronizado"
                        resourcesList.remove(modelRow);
                        resourcesList.add(syncInfo);

                        // Guardar historial para que el cambio persista en el JSON
                        com.gomez.yourdownload.service.DownloadService.saveHistory(resourcesList);

                        this.tableModel.fireTableDataChanged();

                        javax.swing.JOptionPane.showMessageDialog(this,
                                "Download successful! " + destinationFile.getName(),
                                "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    });

                } catch (Exception e) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(this,
                                "Download failed: " + e.getMessage(),
                                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    });
                }
            }).start();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please, select a file to download.",
                    "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
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
