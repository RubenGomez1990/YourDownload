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
    private javax.swing.table.TableRowSorter<DownloadInfoTableModel> sorter;

    public MediaLibrary(MainScreen principal, JPanel originalPanel, List<DownloadInfo> resourcesList, MediaPoller mediaPoller) {
        this.mainScreen = principal;
        this.originalPanel = originalPanel;
        this.resourcesList = resourcesList;
        this.mediaPoller = mediaPoller;

        initComponents();

        // 1. DIMENSIONES Y LAYOUT
        this.setPreferredSize(new java.awt.Dimension(1200, 800));
        this.setLayout(null);

        // 2. ESTÉTICA Y POSICIÓN DE TABLA
        jScrollPaneMedia.setBounds(10, 10, 1160, 600);
        jTableMedia.setRowHeight(35);
        jTableMedia.setShowVerticalLines(false);
        jScrollPaneMedia.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        // 3. POSICIONAMIENTO DE BOTONES (Fila Y=630)
        jLabelFilter.setBounds(10, 630, 70, 30);
        jComboBoxFilter.setBounds(90, 630, 160, 30);
        jButtonSearch.setBounds(260, 630, 150, 40);

        jButtonDelete.setBounds(450, 630, 120, 40);
        jButtonUpload1.setBounds(580, 630, 120, 40);

        jButtonDownload.setBounds(850, 630, 130, 40);
        jButtonBack.setBounds(1000, 630, 130, 40);

        // 4. MODELO Y SORTER
        tableModel = new DownloadInfoTableModel(resourcesList);
        jTableMedia.setModel(tableModel);

        sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        javax.swing.table.TableColumnModel columnModel = jTableMedia.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID: Pequeño
        columnModel.getColumn(1).setPreferredWidth(500);  // Name: El más ancho para ver nombres largos
        columnModel.getColumn(2).setPreferredWidth(100);  // Size
        columnModel.getColumn(3).setPreferredWidth(80);   // Format
        columnModel.getColumn(4).setPreferredWidth(180);  // Download Date
        columnModel.getColumn(5).setPreferredWidth(150);
        jTableMedia.getTableHeader().setResizingAllowed(true);
        jTableMedia.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableMedia.setRowSorter(sorter);

        javax.swing.table.DefaultTableCellRenderer idRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                // Mantenemos tu lógica de "N/A" para IDs nulos
                setText((value == null) ? "N/A" : value.toString());
            }
        };

        idRenderer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTableMedia.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        // 5. ORDEN INICIAL POR ID (Columna 0, Ascendente)
        java.util.List<javax.swing.RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        jTableMedia.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic detectado
                    int row = jTableMedia.getSelectedRow();
                    if (row != -1) {
                        int modelRow = jTableMedia.convertRowIndexToModel(row);
                        DownloadInfo resource = resourcesList.get(modelRow);

                        // Comprobamos si el archivo existe físicamente
                        if (resource.getAbsolutePath() != null) {
                            File file = new File(resource.getAbsolutePath());
                            if (file.exists()) {
                                try {
                                    // REPRODUCIR: Abre el archivo con el reproductor del sistema
                                    java.awt.Desktop.getDesktop().open(file);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "No se pudo abrir el archivo.");
                                }
                            } else {
                                // Si la ruta existe en la lista pero no en el disco, descargamos
                                jButtonDownloadActionPerformed(null);
                            }
                        } else {
                            // Si no tiene ruta (es Network Only), descargamos
                            jButtonDownloadActionPerformed(null);
                        }
                    }
                }
            }
        });

        // Inicializar contenido
        initFiltroComboBox();
        loadAllMediaInfo();
    }

    private void loadAllMediaInfo() {
        new Thread(() -> {
            try {
                // 1. Obtenemos todos los archivos que hay en la nube (Red)
                List<com.gomez.model.Media> networkFiles = this.mediaPoller.getAllMedia();
                if (networkFiles == null) {
                    return;
                }

                // Creamos un mapa para buscar archivos por nombre rápidamente
                Map<String, com.gomez.model.Media> networkMap = new HashMap<>();
                for (com.gomez.model.Media m : networkFiles) {
                    if (m.mediaFileName != null) {
                        networkMap.put(m.mediaFileName.toLowerCase().trim(), m);
                    }
                }

                // 2. Pasamos al hilo de la interfaz para actualizar los datos
                SwingUtilities.invokeLater(() -> {

                    // Lista temporal para los datos limpios
                    List<DownloadInfo> cleanedList = new ArrayList<>();

                    // Set para evitar duplicados por nombre
                    java.util.Set<String> processedNames = new java.util.HashSet<>();

                    // A. Procesamos lo que ya tenemos en local (nuestro historial JSON)
                    for (DownloadInfo localFile : resourcesList) {

                        // --- 1. VERIFICACIÓN DE EXISTENCIA FÍSICA ---
                        // Si el archivo tiene ruta local, verificamos si el archivo existe de verdad
                        if (localFile.getAbsolutePath() != null && !localFile.getAbsolutePath().isEmpty()) {
                            File physicalFile = new File(localFile.getAbsolutePath());
                            if (!physicalFile.exists()) {
                                continue; // Si lo borraste del PC, NO lo añadimos a cleanedList (desaparece)
                            }
                        }

                        String name = localFile.getFileName().toLowerCase().trim();

                        // --- 2. LOGICA DE SINCRONIZACIÓN Y FORMATO ---
                        if (networkMap.containsKey(name)) {
                            com.gomez.model.Media net = networkMap.get(name);
                            localFile.setNetworkId(net.id);
                            localFile.setIsInNetwork(true);

                            // AQUÍ FORZAMOS EL FORMATO LIMPIO (MP4, MP3, etc)
                            localFile.setMimeType(getCleanFormat(net.mediaMimeType, net.mediaFileName));

                            networkMap.remove(name); // Lo quitamos del mapa para que no se repita en el paso B
                        } else {
                            // Si es un archivo local que no está en la red, también limpiamos su formato
                            localFile.setMimeType(getCleanFormat(null, localFile.getFileName()));
                        }

                        if (!processedNames.contains(name)) {
                            cleanedList.add(localFile);
                            processedNames.add(name);
                        }
                    }

                    // B. Añadimos lo que falta (lo que solo está en la red)
                    for (com.gomez.model.Media net : networkMap.values()) {
                        String name = net.mediaFileName.toLowerCase().trim();
                        if (!processedNames.contains(name)) {
                            // Limpiamos formato para los de la nube
                            String format = getCleanFormat(net.mediaMimeType, net.mediaFileName);

                            DownloadInfo netInfo = new DownloadInfo(net.id, net.mediaFileName, (long) net.mediaFileSize, format);
                            cleanedList.add(netInfo);
                            processedNames.add(name);
                        }
                    }

                    // 3. PASO FINAL: Sustituimos la lista vieja por la nueva ya filtrada y formateada
                    resourcesList.clear();
                    resourcesList.addAll(cleanedList);

                    // Notificamos a la tabla
                    tableModel.fireTableDataChanged();
                });

            } catch (Exception e) {
                System.err.println("Error syncing media library: " + e.getMessage());
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

        if (sorter == null) {
            return;
        }

        String selected = (String) jComboBoxFilter.getSelectedItem();

        // Si es "All Types", quitamos cualquier filtro
        if (selected.equals("All Types")) {
            sorter.setRowFilter(null);
        } else {
            // Extraemos solo la extensión (ej: de "Video (MP4)" sacamos "MP4")
            String extension = selected;
            if (selected.contains("MP4")) {
                extension = "MP4";
            } else if (selected.contains("MP3")) {
                extension = "MP3";
            } else if (selected.contains("AVI")) {
                extension = "AVI";
            } else if (selected.contains("WAV")) {
                extension = "WAV";
            }

            // Filtramos en la columna 3 (Format) ignorando mayúsculas
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + extension, 3));
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
        String[] filterTypes = {
            "All Types",
            "Video (MP4)",
            "Audio (MP3)",
            "Video (AVI)",
            "Audio (WAV)"
        };
        jComboBoxFilter.setModel(new javax.swing.DefaultComboBoxModel<>(filterTypes));
    }

    private String getCleanFormat(String mimeType, String fileName) {
        // Si tenemos nombre de archivo, la extensión es lo más fiable
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        }

        // Fallback por si no hay nombre
        if (mimeType != null) {
            if (mimeType.contains("mp4")) {
                return "MP4";
            }
            if (mimeType.contains("mp3") || mimeType.contains("mpeg")) {
                return "MP3";
            }
            if (mimeType.contains("avi")) {
                return "AVI";
            }
        }
        return "N/A";
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
