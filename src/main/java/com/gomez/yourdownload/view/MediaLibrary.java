package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller;
import com.gomez.yourdownload.model.DownloadInfoTableModel;
import com.gomez.yourdownload.model.DownloadInfo;
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

        // 1. DIMENSIONES, LAYOUT Y FONDO
        this.setPreferredSize(new java.awt.Dimension(1200, 800));
        this.setLayout(null);
        this.setBackground(java.awt.Color.WHITE);

        if (jButtonRefresh == null) {
            jButtonRefresh = new javax.swing.JButton("Refresh");
            add(jButtonRefresh);
        }

        java.awt.Color corporateBlue = new java.awt.Color(74, 134, 173);

        // 2. ESTÉTICA DE TABLA Y BORDE AZUL
        jScrollPaneMedia.setBounds(10, 10, 1160, 600);
        // Borde azul para diferenciar la tabla del fondo blanco
        jScrollPaneMedia.setBorder(javax.swing.BorderFactory.createLineBorder(corporateBlue, 1));
        jTableMedia.setRowHeight(35);
        jTableMedia.setShowVerticalLines(false);
        jTableMedia.setSelectionBackground(new java.awt.Color(74, 134, 173, 50));
        jTableMedia.setSelectionForeground(java.awt.Color.BLACK);
        jTableMedia.getTableHeader().setReorderingAllowed(false);

        // 3. PERSONALIZACIÓN DE BOTONES (Rectangulares)
        javax.swing.JButton[] actionButtons = {
            jButtonDelete, jButtonUpload1, jButtonDownload, jButtonRefresh
        };
        for (javax.swing.JButton btn : actionButtons) {
            if (btn != null) {
                btn.setBackground(corporateBlue);
                btn.setForeground(java.awt.Color.WHITE);
                btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                btn.putClientProperty("JButton.buttonType", "square");
                btn.putClientProperty("JComponent.arc", 0);
                btn.setFocusPainted(false);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        }

        // Lupa para la búsqueda
        jLabelSearch.setText("");
        jLabelSearch.setToolTipText("Search by text");
        try {
            jLabelSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search_blue.png")));
        } catch (Exception e) {
            System.err.println("Search icon missing");
        }
        jLabelSearch.setBounds(10, 680, 32, 32);

        jTextFieldSearch.setBounds(100, 680, 160, 30);
        jTextFieldSearch.putClientProperty("JTextField.placeholderText", "Search by name...");
        jTextFieldSearch.putClientProperty("JComponent.arc", 0);

        //Icon buttons
        for (javax.swing.JButton btn : actionButtons) {
            if (btn != null) {
                btn.setBackground(corporateBlue);
                btn.setForeground(java.awt.Color.WHITE);
                // ... resto de tu estilo ...

                // --- ASEGÚRATE DE QUE ESTO ESTÉ AQUÍ ---
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        }
        makeIconOnlyButton(jButtonBack, "/icons/back_blue.png", "Go previous");
        jButtonBack.setBounds(1110, 630, 40, 40);
        makeIconOnlyButton(jButtonRefresh, "/icons/refresh_green.png", "Sync library with server");
        jButtonRefresh.setBounds(260, 630, 32, 32);
        makeIconOnlyButton(jButtonUpload1, "/icons/upload_green.png", "Upload local file to cloud");
        jButtonUpload1.setBounds(297, 630, 32, 32);
        makeIconOnlyButton(jButtonDelete, "/icons/delete_Red.png", "Delete selected file");
        jButtonDelete.setBounds(371, 630, 32, 32);
        makeIconOnlyButton(jButtonDownload, "/icons/download_blue.png", "Download selected file");
        jButtonDownload.setBounds(334, 630, 32, 32);

        jLabelFilter.setBounds(10, 630, 80, 30);
        jComboBoxFilter.setBounds(100, 630, 160, 30);

        // 6. LISTENERS (Implementados directamente en el constructor)
        jButtonRefresh.addActionListener(evt -> {
            jButtonRefresh.setEnabled(false);
            jButtonRefresh.setText("Syncing...");
            loadAllMediaInfo();
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                jButtonRefresh.setEnabled(true);
                jButtonRefresh.setText("Refresh 🔄");
            });
            timer.setRepeats(false);
            timer.start();
        });

        jTextFieldSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltroCombinado();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltroCombinado();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltroCombinado();
            }
        });

        // 7. MODELO, SORTER Y ORDENACIÓN INICIAL (RESTABLECIDO)
        tableModel = new DownloadInfoTableModel(resourcesList);
        jTableMedia.setModel(tableModel);

        sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        javax.swing.table.TableColumnModel columnModel = jTableMedia.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(500);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(180);
        columnModel.getColumn(5).setPreferredWidth(150);

        jTableMedia.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableMedia.setRowSorter(sorter);

        // Renderer para mostrar "N/A" en IDs nulos
        javax.swing.table.DefaultTableCellRenderer idRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText((value == null) ? "N/A" : value.toString());
            }
        };
        idRenderer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTableMedia.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        // --- ORDEN INICIAL POR ID (Lo que se había perdido) ---
        java.util.List<javax.swing.RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        // Listener para doble clic en la tabla
        jTableMedia.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    handleTableDoubleClick();
                }
            }
        });

        initFiltroComboBox();
        loadAllMediaInfo();
    }

    private void loadAllMediaInfo() {
        new Thread(() -> {
            try {
                // 1. Obtenemos todos los archivos que hay en la nube (Red)
                List<com.gomez.model.Media> networkFiles = this.mediaPoller.getAllMedia();
                if (networkFiles == null) {
                    throw new Exception("Server unreachable.");
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
                System.err.println("Sync Error: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    jLabelSearch.setToolTipText("Sync Error: " + e.getMessage());
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPaneMedia = new javax.swing.JScrollPane();
        jTableMedia = new javax.swing.JTable();
        jLabelFilter = new javax.swing.JLabel();
        jComboBoxFilter = new javax.swing.JComboBox<>();
        jButtonDelete = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();
        jButtonUpload1 = new javax.swing.JButton();
        jLabelSearch = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();

        jLabel1.setText("jLabel1");

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

        jLabelFilter.setText("Filter:");
        add(jLabelFilter);
        jLabelFilter.setBounds(10, 230, 40, 30);

        jComboBoxFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterActionPerformed(evt);
            }
        });
        add(jComboBoxFilter);
        jComboBoxFilter.setBounds(50, 230, 120, 30);

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
        add(jLabelSearch);
        jLabelSearch.setBounds(10, 310, 50, 20);

        jTextFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchActionPerformed(evt);
            }
        });
        add(jTextFieldSearch);
        jTextFieldSearch.setBounds(70, 310, 71, 22);
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

        if (sorter != null) {
            aplicarFiltroCombinado();
        }
    }//GEN-LAST:event_jComboBoxFilterActionPerformed

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
                    // Check the error message for specific HTTP codes
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                    
                    if (errorMessage.contains("404") || errorMessage.contains("Not Found")) {
                        writeLog("ERROR 404", "File '" + resource.getFileName() + "' is no longer on the server.");
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(this,
                                    "Error 404: The file was not found on the server.\nIt might have been deleted.",
                                    "File Not Found", javax.swing.JOptionPane.ERROR_MESSAGE);
                        });
                    } else if (errorMessage.contains("403") || errorMessage.contains("Forbidden")) {
                        writeLog("ERROR 403", "Access denied when trying to download '" + resource.getFileName() + "'.");
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(this,
                                    "Error 403: Access Denied.\nYou don't have permission to download this file.",
                                    "Forbidden", javax.swing.JOptionPane.ERROR_MESSAGE);
                        });
                    } else {
                        // Any other general download error
                        writeLog("DOWNLOAD ERROR", "Failed to download '" + resource.getFileName() + "': " + errorMessage);
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(this,
                                    "Download failed: " + errorMessage,
                                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace(); // Keep your original stack trace print
                        });
                    }
                }}).start();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please, select a file to download.",
                    "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDownloadActionPerformed

    private void jButtonUpload1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpload1ActionPerformed
        // 1. Creamos el selector de archivos
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setDialogTitle("Select a file to upload to the Cloud");

        // 2. Abrimos la ventana y comprobamos si el usuario elige un archivo
        int resultado = selector.showOpenDialog(this);

        if (resultado == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File archivoASubir = selector.getSelectedFile();

            // Deshabilitamos el botón temporalmente para evitar doble clic
            jButtonUpload1.setEnabled(false);

            // 3. Hilo secundario para la subida (Network operation)
            new Thread(() -> {
                try {
                    System.out.println("Starting upload: " + archivoASubir.getName());

                    // Llamada al componente Poller para subir a la API
                    // Nota: Asegúrate de que tu MediaPoller tenga implementado el método upload(File f)
                    boolean exito = realizarUploadManual(archivoASubir);

                    if (exito) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "File uploaded successfully!");
                            // Refrescamos la biblioteca para que aparezca el nuevo ID de red
                            loadAllMediaInfo();
                        });
                    } else {
                        throw new Exception("Server rejected the file.");
                    }

                } catch (Exception e) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                } finally {
                    // Volvemos a habilitar el botón
                    javax.swing.SwingUtilities.invokeLater(() -> jButtonUpload1.setEnabled(true));
                }
            }).start();
        }
    }//GEN-LAST:event_jButtonUpload1ActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed

    }//GEN-LAST:event_jTextFieldSearchActionPerformed

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

    private void aplicarFiltroCombinado() {
        String texto = jTextFieldSearch.getText().trim();
        String seleccion = (String) jComboBoxFilter.getSelectedItem();

        List<javax.swing.RowFilter<Object, Object>> filtros = new ArrayList<>();

        // 1. Filtro de Texto (Columna 1: Name)
        if (!texto.isEmpty()) {
            filtros.add(javax.swing.RowFilter.regexFilter("(?i)" + texto, 1));
        }

        // 2. Filtro de Formato (Columna 3: Format)
        if (seleccion != null && !seleccion.equals("All Types")) {
            // Extraemos solo la extensión, ej: de "Video (MP4)" sacamos "MP4"
            String extension = seleccion.contains("(") ? seleccion.substring(seleccion.indexOf("(") + 1, seleccion.indexOf(")")) : seleccion;
            filtros.add(javax.swing.RowFilter.regexFilter("(?i)" + extension, 3));
        }

        if (filtros.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(javax.swing.RowFilter.andFilter(filtros));
        }
    }

    private boolean realizarUploadManual(File archivo) throws Exception {
        String base = this.mediaPoller.getApiUrl();
        if (base == null || base.isEmpty() || base.equals("null")) {
            base = "https://difreenet9.azurewebsites.net";
        }
        base = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;

        // URL exacta de tu captura de Postman
        String urlApi = base + "/api/Files/upload";
        String boundary = "---" + System.currentTimeMillis();

        java.net.URL url = new java.net.URL(urlApi);
        java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String token = this.mediaPoller.getToken();
        if (token != null) {
            con.setRequestProperty("Authorization", "Bearer " + token);
        }

        // Usamos el OutputStream directamente para tener control total
        try (java.io.OutputStream out = con.getOutputStream()) {
            // 1. Campo: File (Binario)
            escribirTexto(out, "--" + boundary + "\r\n");
            escribirTexto(out, "Content-Disposition: form-data; name=\"File\"; filename=\"" + archivo.getName() + "\"\r\n");
            escribirTexto(out, "Content-Type: application/octet-stream\r\n\r\n");
            out.flush();
            java.nio.file.Files.copy(archivo.toPath(), out);
            out.flush();
            escribirTexto(out, "\r\n");

            // 2. Campo: downloadedFromUrl (Texto)
            escribirTexto(out, "--" + boundary + "\r\n");
            escribirTexto(out, "Content-Disposition: form-data; name=\"downloadedFromUrl\"\r\n\r\n");
            escribirTexto(out, "https://youtu.be/manual-upload\r\n"); // Simulamos una URL como en Postman

            // 3. Campo: Container (Texto vacío)
            escribirTexto(out, "--" + boundary + "\r\n");
            escribirTexto(out, "Content-Disposition: form-data; name=\"Container\"\r\n\r\n");
            escribirTexto(out, "\r\n"); // Vacío

            // Cierre final (Importante: dos guiones al final)
            escribirTexto(out, "--" + boundary + "--\r\n");
            out.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("Respuesta del servidor: " + responseCode);

        // Si hay error, intentamos leer por qué (el servidor suele mandar un JSON con el error)
        if (responseCode >= 400) {
            try (java.util.Scanner s = new java.util.Scanner(con.getErrorStream()).useDelimiter("\\A")) {
                System.err.println("Detalle del error: " + (s.hasNext() ? s.next() : "Sin detalle"));
            } catch (Exception e) {
                /* no error detail */ }
        }

        return responseCode == 200 || responseCode == 201;
    }

    private void makeIconOnlyButton(javax.swing.JButton btn, String iconPath, String tooltip) {
        if (btn == null) {
            return;
        }

        btn.setText("");
        btn.setToolTipText(tooltip);

        // --- LÍNEA CLAVE: Cambia el cursor a la "mano" al pasar por encima ---
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        try {
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgURL);
                // Mantenemos el suavizado para que no se vea pixelado
                java.awt.Image scaled = icon.getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                btn.setIcon(new javax.swing.ImageIcon(scaled));
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + iconPath);
        }
    }

    private void escribirTexto(java.io.OutputStream out, String texto) throws java.io.IOException {
        out.write(texto.getBytes("UTF-8"));
    }

    private void handleTableDoubleClick() {
        int row = jTableMedia.getSelectedRow();
        if (row != -1) {
            int modelRow = jTableMedia.convertRowIndexToModel(row);
            DownloadInfo resource = resourcesList.get(modelRow);

            try {
                // CASO 1: El archivo tiene una ruta grabada (ya se descargó alguna vez)
                if (resource.getAbsolutePath() != null && !resource.getAbsolutePath().isEmpty()) {
                    File file = new File(resource.getAbsolutePath());

                    if (file.exists()) {
                        // Si el archivo físico existe, lo abrimos con el reproductor del sistema
                        java.awt.Desktop.getDesktop().open(file);
                    } else {
                        // Si la ruta existe pero el archivo no está en el disco (borrado manual)
                        int resp = JOptionPane.showConfirmDialog(this,
                                "The file is missing from your disk. Do you want to download it again?",
                                "File Missing", JOptionPane.YES_NO_OPTION);

                        if (resp == JOptionPane.YES_OPTION) {
                            jButtonDownloadActionPerformed(null);
                        }
                    }
                } // CASO 2: El archivo NO tiene ruta local (Es "Network Only")
                // Esta es la parte que se nos había quedado fuera
                else {
                    jButtonDownloadActionPerformed(null);
                }

            } catch (java.io.IOException ex) {
                writeLog("PLAYER ERROR", "No compatible player found or access denied for: " + resource.getFileName());
                JOptionPane.showMessageDialog(this,
                        "No compatible player found or access denied.",
                        "Execution Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                writeLog("UNEXPECTED ERROR", "Double click execution failed: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage());
            }
        }
    }
    
    private void writeLog(String errorType, String details) {
        try {
            java.io.File logFile = new java.io.File("yourdownload_errors.log"); 
            java.io.FileWriter fw = new java.io.FileWriter(logFile, true); 
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            java.io.PrintWriter out = new java.io.PrintWriter(bw);
            
            // Format: YYYY/MM/DD HH:mm:ss
            String timeStamp = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date());
            
            out.println("[" + timeStamp + "] [" + errorType + "] " + details);
            out.close();
            
        } catch (Exception e) {
            System.err.println("Critical Error: Could not write to log file. " + e.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonUpload1;
    private javax.swing.JComboBox<String> jComboBoxFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelFilter;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JScrollPane jScrollPaneMedia;
    private javax.swing.JTable jTableMedia;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
private javax.swing.JButton jButtonRefresh;
}
