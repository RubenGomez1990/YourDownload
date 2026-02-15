package com.gomez.yourdownload.view;

import com.gomez.yourdownload.model.DownloadInfo;
import com.gomez.yourdownload.service.DownloadService;
import com.gomez.yourdownload.view.MediaLibrary;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.gomez.component.MediaPoller;

/**
 *
 * @author Rubén Gómez Hernández
 */
public class MainScreen extends javax.swing.JFrame {

    private String destinyPath = "";
    private JPanel originalPanel;
    private List<DownloadInfo> resourcesList;
    private String jwtToken;
    private String currentView = "MAIN";
    java.awt.Color azulLogin = new java.awt.Color(74, 134, 173);

    public MainScreen(String token, MediaPoller pollerInstance) {
        this.jwtToken = token;// Almacenamos el token JWT para usarlo en descargas, etc.
        this.mediaPoller1 = pollerInstance;

        // UI creation
        resourcesList = DownloadService.loadHistory();
        initComponents();
        originalPanel = (JPanel) getContentPane();

        // Style settings buttons
        jButtonDownload.setBackground(azulLogin);
        jButtonDownload.setForeground(java.awt.Color.WHITE);
        jButtonDownload.putClientProperty("JButton.buttonType", "square");
        jButtonDownload.putClientProperty("JComponent.arc", 0);

        jButtonDownload.setToolTipText("Start downloading the video/audio");
        jButtonDownload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
// Para poner el icono (Asegúrate de tener la carpeta src/main/resources/icons/)
        try {
            jButtonDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/download_white.png")));
            jButtonDownload.setText("Download"); // Puedes dejar el texto o quitarlo si el icono es muy claro
        } catch (Exception e) {
            System.err.println("Icon not found");
        }

        jButtonLibrary.setBackground(azulLogin);
        jButtonLibrary.setForeground(java.awt.Color.WHITE);
        jButtonLibrary.putClientProperty("JButton.buttonType", "square");
        jButtonLibrary.putClientProperty("JComponent.arc", 0);

        jButtonLibrary.setToolTipText("Open your saved files and network library");
        jButtonLibrary.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        try {
            jButtonLibrary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/library_white.png")));
            jButtonLibrary.setText("Library");
        } catch (Exception e) {
            System.err.println("Icon not found");
        }

        jButtonShowLog.setBackground(azulLogin);
        jButtonShowLog.setForeground(java.awt.Color.WHITE);
        jButtonShowLog.putClientProperty("JButton.buttonType", "square");
        jButtonShowLog.putClientProperty("JComponent.arc", 0);
        jButtonShowLog.setToolTipText("Toggle console visibility");
        jButtonShowLog.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonShowLog.setBackground(azulLogin);
        jButtonShowLog.setForeground(java.awt.Color.WHITE);
        jButtonShowLog.putClientProperty("JButton.buttonType", "square");
        jButtonShowLog.putClientProperty("JComponent.arc", 0);
        jLabelDownloadStatus.setForeground(azulLogin);

        jButtonChange.setBackground(azulLogin);
        jButtonChange.setForeground(java.awt.Color.WHITE);
        jButtonChange.putClientProperty("JButton.buttonType", "square");
        jButtonChange.putClientProperty("JComponent.arc", 0);

        // Settings button radio
        jRadioMP4.setSelected(true);
        setPanelEnabled(jPanelQuality, true);  // Calidad vídeo activa
        jRadioButton1080.setSelected(true);
        jPanelAudioQuality.setVisible(false);
        jPanelConsole.setVisible(false);

        String userHome = System.getProperty("user.home");
        File downloadsFolder = new File(userHome + File.separator + "Downloads");
        File desktopFolder = new File(userHome + File.separator + "Desktop");

        if (downloadsFolder.exists()) {
            this.destinyPath = downloadsFolder.getAbsolutePath();
        } else if (desktopFolder.exists()) {
            this.destinyPath = desktopFolder.getAbsolutePath();
        } else {
            this.destinyPath = userHome;
        }

        jLabelSave.setText("Saved at: " + this.destinyPath);
        jButtonSavePath.setVisible(false);
        jButtonChange.setVisible(true);

        if (token == null || token.isEmpty()) {
            // --- CASO LOGIN ---
            showLoginScreen(); // Este método ahora se encargará de encoger la ventana
        } else {
            // --- CASO MAIN ---
            initMediaPoller(token);
            this.setSize(1024, 330); // Tamaño grande para descargas
            this.setLocationRelativeTo(null);
        }
        updateIconState(this.mediaPoller1.isRunning());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                String[] options = {"Yes", "No"};
                int result = JOptionPane.showOptionDialog(MainScreen.this,
                        "Are you sure you want to exit?",
                        "Exit", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]);

                if (result == 0) {
                    com.gomez.yourdownload.service.DownloadService.saveHistory(resourcesList);
                    System.exit(0);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupQuality = new javax.swing.ButtonGroup();
        buttonGroupAQ = new javax.swing.ButtonGroup();
        buttonGroupOutput = new javax.swing.ButtonGroup();
        jLabelWelcome = new javax.swing.JLabel();
        jPanelWeb = new javax.swing.JPanel();
        jLabelUrl = new javax.swing.JLabel();
        jTextFieldUrl = new javax.swing.JTextField();
        jPanelSubtitle = new javax.swing.JPanel();
        jLabelSubtitles = new javax.swing.JLabel();
        jCheckBoxSubtitlesYes = new javax.swing.JCheckBox();
        jPanelFormat = new javax.swing.JPanel();
        jLabelFormat = new javax.swing.JLabel();
        jPanelSave = new javax.swing.JPanel();
        jLabelSave = new javax.swing.JLabel();
        jButtonSavePath = new javax.swing.JButton();
        jButtonChange = new javax.swing.JButton();
        jPanelProgress = new javax.swing.JPanel();
        jLabelProgress = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jPanelConsole = new javax.swing.JPanel();
        jScrollPaneConsole = new javax.swing.JScrollPane();
        jTextAreaConsole = new javax.swing.JTextArea();
        jPanelDownload = new javax.swing.JPanel();
        jButtonDownload = new javax.swing.JButton();
        jPanelLibrary = new javax.swing.JPanel();
        jButtonLibrary = new javax.swing.JButton();
        jPanelAudioQuality = new javax.swing.JPanel();
        jRadioButtonHQ = new javax.swing.JRadioButton();
        jRadioButtonHigh = new javax.swing.JRadioButton();
        jLabelAQ = new javax.swing.JLabel();
        mediaPoller1 = new com.gomez.component.MediaPoller();
        jButtonShowLog = new javax.swing.JButton();
        jLabelStatus = new javax.swing.JLabel();
        jRadioMP4 = new javax.swing.JRadioButton();
        jRadioButtonAVI = new javax.swing.JRadioButton();
        jRadioButtonMP3 = new javax.swing.JRadioButton();
        jPanelQuality = new javax.swing.JPanel();
        jLabelQuality = new javax.swing.JLabel();
        jRadioButton1080 = new javax.swing.JRadioButton();
        jRadioButton720 = new javax.swing.JRadioButton();
        jRadioButton480 = new javax.swing.JRadioButton();
        jLabelDownloadStatus = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemLogout = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("YourDownload");
        getContentPane().setLayout(null);

        jLabelWelcome.setText("Welcome! Download videos with no barriers!");
        getContentPane().add(jLabelWelcome);
        jLabelWelcome.setBounds(10, 10, 240, 16);

        jPanelWeb.setLayout(null);

        jLabelUrl.setText("URL site:");
        jPanelWeb.add(jLabelUrl);
        jLabelUrl.setBounds(0, 0, 50, 20);

        jTextFieldUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUrlActionPerformed(evt);
            }
        });
        jPanelWeb.add(jTextFieldUrl);
        jTextFieldUrl.setBounds(110, 0, 670, 30);

        getContentPane().add(jPanelWeb);
        jPanelWeb.setBounds(10, 50, 780, 30);

        jPanelSubtitle.setLayout(null);

        jLabelSubtitles.setText("Download Subtitles:");
        jPanelSubtitle.add(jLabelSubtitles);
        jLabelSubtitles.setBounds(0, 0, 110, 20);

        jCheckBoxSubtitlesYes.setText("Yes");
        jCheckBoxSubtitlesYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSubtitlesYesActionPerformed(evt);
            }
        });
        jPanelSubtitle.add(jCheckBoxSubtitlesYes);
        jCheckBoxSubtitlesYes.setBounds(120, 0, 84, 20);

        getContentPane().add(jPanelSubtitle);
        jPanelSubtitle.setBounds(370, 90, 170, 30);

        jPanelFormat.setLayout(null);

        jLabelFormat.setText("Output Format:");
        jPanelFormat.add(jLabelFormat);
        jLabelFormat.setBounds(0, 0, 90, 20);

        getContentPane().add(jPanelFormat);
        jPanelFormat.setBounds(10, 130, 90, 30);

        jPanelSave.setLayout(null);

        jLabelSave.setText("Destination folder:");
        jPanelSave.add(jLabelSave);
        jLabelSave.setBounds(0, 0, 260, 20);

        jButtonSavePath.setText("Choose");
        jButtonSavePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSavePathActionPerformed(evt);
            }
        });
        jPanelSave.add(jButtonSavePath);
        jButtonSavePath.setBounds(110, 0, 80, 20);

        jButtonChange.setText("Change");
        jButtonChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeActionPerformed(evt);
            }
        });
        jPanelSave.add(jButtonChange);
        jButtonChange.setBounds(220, 0, 72, 20);

        getContentPane().add(jPanelSave);
        jPanelSave.setBounds(10, 170, 440, 20);

        jPanelProgress.setLayout(null);

        jLabelProgress.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelProgress.setText("Completed:");
        jPanelProgress.add(jLabelProgress);
        jLabelProgress.setBounds(0, -10, 100, 40);
        jPanelProgress.add(jProgressBar);
        jProgressBar.setBounds(110, 0, 670, 20);

        getContentPane().add(jPanelProgress);
        jPanelProgress.setBounds(10, 210, 780, 30);

        jPanelConsole.setLayout(null);

        jTextAreaConsole.setColumns(20);
        jTextAreaConsole.setRows(5);
        jScrollPaneConsole.setViewportView(jTextAreaConsole);

        jPanelConsole.add(jScrollPaneConsole);
        jScrollPaneConsole.setBounds(0, 0, 990, 430);

        getContentPane().add(jPanelConsole);
        jPanelConsole.setBounds(10, 280, 990, 460);

        jPanelDownload.setLayout(null);

        jButtonDownload.setText("Download!");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });
        jPanelDownload.add(jButtonDownload);
        jButtonDownload.setBounds(0, 0, 170, 60);

        getContentPane().add(jPanelDownload);
        jPanelDownload.setBounds(830, 40, 170, 60);

        jPanelLibrary.setLayout(null);

        jButtonLibrary.setText("Media Library");
        jButtonLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLibraryActionPerformed(evt);
            }
        });
        jPanelLibrary.add(jButtonLibrary);
        jButtonLibrary.setBounds(0, 0, 170, 60);

        getContentPane().add(jPanelLibrary);
        jPanelLibrary.setBounds(830, 110, 170, 60);

        jPanelAudioQuality.setLayout(null);

        buttonGroupAQ.add(jRadioButtonHQ);
        jRadioButtonHQ.setText("HQ (Best)");
        jRadioButtonHQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonHQActionPerformed(evt);
            }
        });
        jPanelAudioQuality.add(jRadioButtonHQ);
        jRadioButtonHQ.setBounds(50, 0, 100, 21);

        buttonGroupAQ.add(jRadioButtonHigh);
        jRadioButtonHigh.setText("Balanced (High)");
        jRadioButtonHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonHighActionPerformed(evt);
            }
        });
        jPanelAudioQuality.add(jRadioButtonHigh);
        jRadioButtonHigh.setBounds(130, 0, 130, 21);

        jLabelAQ.setText("Audio:");
        jPanelAudioQuality.add(jLabelAQ);
        jLabelAQ.setBounds(0, 0, 80, 20);

        getContentPane().add(jPanelAudioQuality);
        jPanelAudioQuality.setBounds(370, 130, 270, 20);

        mediaPoller1.setApiUrl("https://difreenet9.azurewebsites.net");
        getContentPane().add(mediaPoller1);
        mediaPoller1.setBounds(310, 10, 150, 18);

        jButtonShowLog.setText("Log");
        jButtonShowLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowLogActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonShowLog);
        jButtonShowLog.setBounds(10, 240, 70, 23);

        jLabelStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelStatusMouseClicked(evt);
            }
        });
        getContentPane().add(jLabelStatus);
        jLabelStatus.setBounds(980, 10, 0, 0);

        buttonGroupOutput.add(jRadioMP4);
        jRadioMP4.setText("mp4");
        jRadioMP4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioMP4ActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioMP4);
        jRadioMP4.setBounds(120, 130, 47, 21);

        buttonGroupOutput.add(jRadioButtonAVI);
        jRadioButtonAVI.setText("avi");
        jRadioButtonAVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonAVIActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonAVI);
        jRadioButtonAVI.setBounds(180, 130, 38, 21);

        buttonGroupOutput.add(jRadioButtonMP3);
        jRadioButtonMP3.setText("mp3 (audio only)");
        jRadioButtonMP3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMP3ActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonMP3);
        jRadioButtonMP3.setBounds(230, 130, 120, 21);

        jPanelQuality.setLayout(null);

        jLabelQuality.setText("Output Resolution:");
        jPanelQuality.add(jLabelQuality);
        jLabelQuality.setBounds(0, 0, 100, 20);

        buttonGroupQuality.add(jRadioButton1080);
        jRadioButton1080.setText("1080p");
        jRadioButton1080.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1080ActionPerformed(evt);
            }
        });
        jPanelQuality.add(jRadioButton1080);
        jRadioButton1080.setBounds(110, 0, 60, 21);

        buttonGroupQuality.add(jRadioButton720);
        jRadioButton720.setText("720p");
        jRadioButton720.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton720ActionPerformed(evt);
            }
        });
        jPanelQuality.add(jRadioButton720);
        jRadioButton720.setBounds(170, 0, 50, 21);

        buttonGroupQuality.add(jRadioButton480);
        jRadioButton480.setText("480p");
        jPanelQuality.add(jRadioButton480);
        jRadioButton480.setBounds(220, 0, 50, 21);

        getContentPane().add(jPanelQuality);
        jPanelQuality.setBounds(10, 90, 270, 20);

        jLabelDownloadStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelDownloadStatus.setForeground(new java.awt.Color(0, 153, 51));
        getContentPane().add(jLabelDownloadStatus);
        jLabelDownloadStatus.setBounds(150, 240, 670, 20);

        jMenuFile.setText("File");

        jMenuItemLogout.setText("Logout");
        jMenuItemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLogoutActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemLogout);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuEdit.setText("Edit");

        jMenuItemPreferences.setText("Preferences");
        jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreferencesActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemPreferences);

        jMenuBar.add(jMenuEdit);

        jMenuHelp.setText("Help");

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUrlActionPerformed

    private void jRadioButton1080ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1080ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1080ActionPerformed

    private void jCheckBoxSubtitlesYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSubtitlesYesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxSubtitlesYesActionPerformed

    private void jButtonSavePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSavePathActionPerformed
        // Filechooser para seleccionar una ruta
        JFileChooser pathSelector = new JFileChooser();
        pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = pathSelector.showOpenDialog(this); // selecciona la ruta 
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = pathSelector.getSelectedFile();
            destinyPath = folder.getAbsolutePath();

            jLabelSave.setText("Saved at: " + destinyPath);

            jButtonSavePath.setVisible(false);
            jButtonChange.setVisible(true);
        }
    }//GEN-LAST:event_jButtonSavePathActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        String[] options = {"Yes", "No"};
        int result = JOptionPane.showOptionDialog(this, "Are you sure you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);

        if (result == 0) {
            DownloadService.saveHistory(resourcesList);
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        About dialog = new About(this, true); // 'this' es tu JFrame principal
        dialog.setLocationRelativeTo(this); // Centrado respecto a la ventana principal
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreferencesActionPerformed
        javax.swing.JPanel currentPanel = (javax.swing.JPanel) this.getContentPane();
        setContentPane(new PreferencesPanel(this, currentPanel));

        revalidate();
        repaint();
    }//GEN-LAST:event_jMenuItemPreferencesActionPerformed

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        final String url = jTextFieldUrl.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL");
            return;
        }

        jButtonDownload.setEnabled(false);
        jTextAreaConsole.setText("");
        jProgressBar.setValue(0);
        jLabelDownloadStatus.setText("Downloading... ");
        jLabelDownloadStatus.setVisible(true);

        // 1. Determinamos formato y calidad
        final String selectedFormat;
        if (jRadioButton1080.isSelected()) {
            selectedFormat = "bestvideo[height<=1080]+bestaudio/best";
        } else if (jRadioButton720.isSelected()) {
            selectedFormat = "bestvideo[height<=720]+bestaudio/best";
        } else {
            selectedFormat = "bestvideo[height<=480]+bestaudio/best";
        }

        final String targetExt;
        if (jRadioButtonMP3.isSelected()) {
            targetExt = "mp3";
        } else if (jRadioButtonAVI.isSelected()) {
            targetExt = "avi";
        } else {
            targetExt = "mp4";
        }

        new Thread(() -> {
            try {
                String binariesPath = getBinariesPath();
                File ytDlpExe = new File(binariesPath);

                // Usamos un identificador único para esta descarga (epoch) para encontrar el archivo luego
                String timestampId = String.valueOf(System.currentTimeMillis() / 1000);

                List<String> command = new ArrayList<>();
                command.add(ytDlpExe.getAbsolutePath());
                command.add("--ffmpeg-location");
                command.add(ytDlpExe.getParent());
                // ... (tus otros argumentos: cookies, js-runtimes, etc)

                if (targetExt.equals("mp3")) {
                    command.add("-x");
                    command.add("--audio-format");
                    command.add("mp3");
                } else {
                    command.add("-f");
                    command.add(selectedFormat);
                    command.add("--recode-video");
                    command.add(targetExt);
                }

                // Plantilla de nombre: Título + ID temporal
                String nameTemplate = "%(title)s_" + timestampId + ".%(ext)s";
                File outputFileTemplate = new File(destinyPath, nameTemplate);
                command.add("-o");
                command.add(outputFileTemplate.getAbsolutePath());
                command.add(url);

                Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String outputLine = line;
                    SwingUtilities.invokeLater(() -> {
                        jTextAreaConsole.append(outputLine + "\n");
                        updateProgressBar(outputLine);
                    });
                }
                process.waitFor();

                // --- LA CLAVE: BUSQUEDA DEL ARCHIVO FINAL ---
                // Buscamos en la carpeta cualquier archivo que contenga nuestro ID único y la extensión deseada
                File folder = new File(destinyPath);
                File[] matches = folder.listFiles((dir, name) -> name.contains(timestampId) && name.toLowerCase().endsWith("." + targetExt));

                if (matches != null && matches.length > 0) {
                    File finalFile = matches[0];

                    // Registro en la lista
                    DownloadInfo info = new DownloadInfo(finalFile.getAbsolutePath(), new Date(), finalFile.length(), targetExt.toUpperCase());
                    resourcesList.add(info);
                    DownloadService.saveHistory(resourcesList);

                    SwingUtilities.invokeLater(() -> {
                        jLabelDownloadStatus.setText("Download Complete!  (" + finalFile.getName() + ")");
                        jLabelDownloadStatus.setForeground(azulLogin); // Usamos el azul corporativo para el texto
                        jLabelDownloadStatus.setText("Successfully saved to your library");
                    });
                } else {
                    throw new Exception("File not found after download process.");
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    jLabelDownloadStatus.setText("Error in download ");
                    jLabelDownloadStatus.setForeground(java.awt.Color.RED);
                    jTextAreaConsole.append("\nERROR: " + e.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> jButtonDownload.setEnabled(true));
            }
        }).start();
    }//GEN-LAST:event_jButtonDownloadActionPerformed

    private void jButtonChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeActionPerformed
        JFileChooser pathSelector = new JFileChooser();
        pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = pathSelector.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = pathSelector.getSelectedFile();
            destinyPath = folder.getAbsolutePath();

            jLabelSave.setText("Saved at: " + destinyPath);
            // No necesitas cambiar visibilidad aquí
        }
    }//GEN-LAST:event_jButtonChangeActionPerformed

    private void jButtonLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLibraryActionPerformed
        showLibrary();
    }//GEN-LAST:event_jButtonLibraryActionPerformed

    private void jRadioButton720ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton720ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton720ActionPerformed

    private void jRadioButtonHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonHighActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonHighActionPerformed

    private void jRadioButtonHQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonHQActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonHQActionPerformed

    private void jMenuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLogoutActionPerformed
// 1. Borrar la persistencia de la sesión
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("YourDownloadApp");
        prefs.remove("jwt_token");
        prefs.remove("email");

        // 2. EN LUGAR DE CERRAR, CAMBIAMOS EL PANEL
        // No hagas dispose(). Simplemente muestra el panel de login.
        showLoginScreen();
    }//GEN-LAST:event_jMenuItemLogoutActionPerformed

    private void jButtonShowLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowLogActionPerformed

        if (!jPanelConsole.isVisible()) {
            // MODO DETALLADO: Mostramos consola y agrandamos ventana
            jPanelConsole.setVisible(true);
            this.setSize(1024, 800);
            jButtonShowLog.setText("Hide");
        } else {
            // MODO COMPACTO: Ocultamos consola y encogemos ventana
            jPanelConsole.setVisible(false);
            this.setSize(1024, 330);
            jButtonShowLog.setText("Log");
        }

        // Refrescamos la UI para que los cambios se apliquen visualmente
        this.revalidate();
        this.repaint(); // TODO add your handling code here:
    }//GEN-LAST:event_jButtonShowLogActionPerformed

    private void jLabelStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelStatusMouseClicked
        togglePolling();// TODO add your handling code here:
    }//GEN-LAST:event_jLabelStatusMouseClicked

    private void jRadioMP4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioMP4ActionPerformed
        // Re-activamos el panel de calidad de vídeo
        setPanelEnabled(jPanelQuality, true);

        // Ocultamos el panel de audio
        jPanelAudioQuality.setVisible(false);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioMP4ActionPerformed

    private void jRadioButtonMP3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMP3ActionPerformed
        setPanelEnabled(jPanelQuality, false);

        // Mostramos el panel de audio (que ahora está al lado)
        jPanelAudioQuality.setVisible(true);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioButtonMP3ActionPerformed

    private void jRadioButtonAVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonAVIActionPerformed
        // Re-activamos el panel de calidad de vídeo
        setPanelEnabled(jPanelQuality, true);

        // Ocultamos el panel de audio
        jPanelAudioQuality.setVisible(false);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioButtonAVIActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            // 1. Forzamos Segoe UI (La fuente de Windows 11/Azure)
            java.awt.Font globalFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);

            // 2. Personalización profunda antes del Setup
            javax.swing.UIManager.put("Component.focusWidth", 1); // Bordes de enfoque más finos
            javax.swing.UIManager.put("Component.innerFocusWidth", 0);
            javax.swing.UIManager.put("Button.innerFocusWidth", 0);

            // El azul corporativo que definimos antes
            javax.swing.UIManager.put("Component.accentColor", new java.awt.Color(74, 134, 173));

            // 3. Aplicamos la fuente a TODO
            java.util.Enumeration<Object> keys = javax.swing.UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = javax.swing.UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    javax.swing.UIManager.put(key, new javax.swing.plaf.FontUIResource(globalFont));
                }
            }

            // 4. USAMOS INTELLIJ LAF (Es más bonito y profesional que el Light normal)
            com.formdev.flatlaf.FlatIntelliJLaf.setup();

        } catch (Exception ex) {
            System.err.println("Theme Error: " + ex.getMessage());
        }

        // 2. Ejecución de la aplicación
        java.awt.EventQueue.invokeLater(() -> {
            // Obtenemos el token guardado
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("YourDownloadApp");
            String token = prefs.get("jwt_token", null);

            // CREACIÓN DEL POLLER (Asegúrate de que el nombre de la clase coincida con tu componente)
            com.gomez.component.MediaPoller globalPoller = new com.gomez.component.MediaPoller();
            globalPoller.setApiUrl("https://difreenet9.azurewebsites.net/");

            // Abrimos la pantalla principal
            new MainScreen(token, globalPoller).setVisible(true);
        });
    }

    private void updateProgressBar(String line) {
        Pattern pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)%");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            double percent = Double.parseDouble(matcher.group(1));
            jProgressBar.setValue((int) percent);
        }
    }

    /**
     * Determina un tipo MIME (formato de contenido) simple basado en la
     * extensión del archivo. * Esta función es auxiliar para categorizar el
     * archivo descargado (MP4, AVI, MP3) para la biblioteca.
     *
     * @param fileName El nombre completo del archivo (e.g., "MiVideo.mp4").
     * @return El String que representa el tipo MIME estándar (e.g.,
     * "video/mp4").
     */
    private String obtainMimeSimple(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "FILE";
        }
        // Extraemos lo que hay después del punto y lo pasamos a MAYÚSCULAS
        // Esto asegura que si el archivo es .mp3, el formato sea "MP3"
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }

    private String getBinariesPath() {
        Preferences prefs = Preferences.userRoot().node("PreferencesPanel");
        final String PREFS_KEY = "binariesPath";

        String storedPath = prefs.get(PREFS_KEY, "");
        if (!storedPath.isEmpty() && new File(storedPath).exists()) {
            return storedPath; // ¡La configuración manual funciona!
        }

        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isEmpty()) {
            String appDataPath = localAppData + File.separator + "yt-dlp.exe";
            if (new File(appDataPath).exists()) {
                prefs.put(PREFS_KEY, appDataPath);
                return appDataPath;
            }
        }

        String userHomePath = System.getProperty("user.home") + File.separator + "yt-dlp.exe";
        if (new File(userHomePath).exists()) {
            prefs.put(PREFS_KEY, userHomePath);
            return userHomePath;
        }
        return "";
    }

    private void showLibrary() {
        this.currentView = "LIBRARY"; // Guardamos que estamos en la librería
        this.setSize(1200, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        MediaLibrary libraryPanel = new MediaLibrary(this, originalPanel, resourcesList, this.mediaPoller1);
        setContentPane(libraryPanel);
        revalidate();
        repaint();
    }

    private void initMediaPoller(String token) {
        if (this.mediaPoller1 == null) {
            System.err.println("Mediapoller is null");
            return;
        }

        if (token != null && !token.isEmpty()) {
            this.mediaPoller1.setToken(token);
        }

        this.mediaPoller1.setRunning(true);
        this.mediaPoller1.addNewMediaListener(new com.gomez.component.NewMediaListener() {
            @Override
            public void onNewMediaDetected(com.gomez.component.NewMediaEvent event) {
                handleNewFilesFound(event);
            }
        });
        mediaPoller1.setSize(0, 0);
        updateIconState(this.mediaPoller1.isRunning());
    }

    private void handleNewFilesFound(final com.gomez.component.NewMediaEvent event) {
        //Solo archivos que no están en la biblioteca local
        List<com.gomez.model.Media> trulyNewFiles = new ArrayList<>();

        for (com.gomez.model.Media remoteFile : event.getNewFiles()) {
            // Comprobamos si el archivo ya existe en nuestra lista
            boolean alreadyExists = resourcesList.stream().anyMatch(local
                    -> (local.getNetworkId() != null && local.getNetworkId().equals(remoteFile.id))
                    || (local.getFileName() != null && local.getFileName().equalsIgnoreCase(remoteFile.mediaFileName))
            );

            if (!alreadyExists) {
                trulyNewFiles.add(remoteFile);
            }
        }

        // Si después de filtrar no queda nada nuevo, salimos silenciosamente
        if (trulyNewFiles.isEmpty()) {
            return;
        }

        //LOG DE DETECCIÓN REAL-TIME
        int newCount = trulyNewFiles.size();
        String firstFileName = trulyNewFiles.get(0).mediaFileName;
        System.out.println("Poller (Real-time): detected [" + newCount + "] new files since last check.");

        // 3. ALERTA DE INTERFAZ (Swing Thread)
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object[] options = {"Download", "Close"};
                int result = javax.swing.JOptionPane.showOptionDialog(
                        MainScreen.this,
                        "New files detected on server!"
                        + "\nFile: " + firstFileName
                        + "\nDo you want to download them now?",
                        "Live Detection",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0]
                );

                if (result == 0) {
                    // 4. HILO DE DESCARGA
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int successCount = 0;
                            for (com.gomez.model.Media mediaFile : trulyNewFiles) {
                                try {
                                    java.io.File destination = new java.io.File(destinyPath, mediaFile.mediaFileName);
                                    mediaPoller1.download(mediaFile.id, destination);

                                    com.gomez.yourdownload.model.DownloadInfo newDownload = new com.gomez.yourdownload.model.DownloadInfo(
                                            destination.getAbsolutePath(),
                                            new java.util.Date(),
                                            destination.length(),
                                            mediaFile.mediaMimeType
                                    );
                                    newDownload.setNetworkId(mediaFile.id);
                                    newDownload.setIsInNetwork(true);

                                    resourcesList.add(newDownload);
                                    successCount++;
                                } catch (Exception e) {
                                    System.err.println("Download error: " + e.getMessage());
                                }
                            }

                            if (successCount > 0) {
                                com.gomez.yourdownload.service.DownloadService.saveHistory(resourcesList);
                                final int finalSuccess = successCount;
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    showLibrary();
                                    javax.swing.JOptionPane.showMessageDialog(MainScreen.this,
                                            "Added " + finalSuccess + " files from server.");
                                });
                            }
                        }
                    }).start();
                } else {
                    /* * SOLUCIÓN AL SOLAPAMIENTO: Si el usuario cierra el aviso sin descargar, 
                 * añadimos los archivos a la lista como "Cloud Only" para que el Poller 
                 * no vuelva a avisar de ellos en la próxima vuelta de 30 segundos.
                     */
                    for (com.gomez.model.Media m : trulyNewFiles) {
                        com.gomez.yourdownload.model.DownloadInfo acknowledged = new com.gomez.yourdownload.model.DownloadInfo(
                                m.id, m.mediaFileName, (long) m.mediaFileSize, m.mediaMimeType
                        );
                        resourcesList.add(acknowledged);
                    }
                    System.out.println("Poller: Files acknowledged but ignored by user.");
                }
            }
        });
    }

    private void showLoginScreen() {
        this.setJMenuBar(null);
        this.setResizable(false);
        LoginPanel login = new LoginPanel(this, this.mediaPoller1);
        this.setContentPane(login);
        this.setSize(500, 320);
        this.setLocationRelativeTo(null);
        this.revalidate();
        this.repaint();
    }

    public void loginSuccessful(String token) { // Si se pone bien la contraseña llama a este método
        this.jwtToken = token;

        //Restauramos el diseño original
        this.setJMenuBar(jMenuBar);
        this.setContentPane(originalPanel);
        jPanelConsole.setVisible(false);
        this.setSize(1024, 330);
        this.setLocationRelativeTo(null);
        this.revalidate();
        this.repaint();
        initMediaPoller(token);
    }

    private void togglePolling() {
        boolean running = this.mediaPoller1.isRunning();

        boolean newState = !running;
        this.mediaPoller1.setRunning(newState);

        updateIconState(newState);

        System.out.println("Poller changed to: " + (newState ? "On" : "Off"));
    }

    private void updateIconState(boolean on) {
        String iconPath = on ? "/icons/status_on.png" : "/icons/status_off.png";
        try {
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgURL);
                jLabelStatus.setIcon(icon);

                jLabelStatus.setSize(24, 24);
                jLabelStatus.setBounds(960, 10, 24, 24);

                jLabelStatus.setToolTipText(on ? "Server Sync: ACTIVE" : "Server Sync: STOPPED");
                jLabelStatus.setText("");
            }
        } catch (Exception e) {
            jLabelStatus.setText(on ? "V" : "X");
        }
    }

    private void setPanelEnabled(javax.swing.JPanel panel, boolean isEnabled) {
        panel.setEnabled(isEnabled);
        for (java.awt.Component comp : panel.getComponents()) {
            comp.setEnabled(isEnabled);
        }
    }
    
    private void makeIconOnlyButton(javax.swing.JButton btn, String iconPath, String tooltip) {
    btn.setText(""); // Quitamos el texto
    btn.setToolTipText(tooltip); // Añadimos la ayuda visual
    btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); // Gesto de la mano
    
    // Quitamos la estética de botón estándar
    btn.setBorderPainted(false); 
    btn.setContentAreaFilled(false); 
    btn.setFocusPainted(false);
    btn.setOpaque(false);

    // Cargamos el icono
    try {
        java.net.URL imgURL = getClass().getResource(iconPath);
        if (imgURL != null) {
            btn.setIcon(new javax.swing.ImageIcon(imgURL));
        }
    } catch (Exception e) {
        System.err.println("Icon not found: " + iconPath);
    }
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupAQ;
    private javax.swing.ButtonGroup buttonGroupOutput;
    private javax.swing.ButtonGroup buttonGroupQuality;
    private javax.swing.JButton jButtonChange;
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonLibrary;
    private javax.swing.JButton jButtonSavePath;
    private javax.swing.JButton jButtonShowLog;
    private javax.swing.JCheckBox jCheckBoxSubtitlesYes;
    private javax.swing.JLabel jLabelAQ;
    private javax.swing.JLabel jLabelDownloadStatus;
    private javax.swing.JLabel jLabelFormat;
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JLabel jLabelQuality;
    private javax.swing.JLabel jLabelSave;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelSubtitles;
    private javax.swing.JLabel jLabelUrl;
    private javax.swing.JLabel jLabelWelcome;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemLogout;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JPanel jPanelAudioQuality;
    private javax.swing.JPanel jPanelConsole;
    private javax.swing.JPanel jPanelDownload;
    private javax.swing.JPanel jPanelFormat;
    private javax.swing.JPanel jPanelLibrary;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelQuality;
    private javax.swing.JPanel jPanelSave;
    private javax.swing.JPanel jPanelSubtitle;
    private javax.swing.JPanel jPanelWeb;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JRadioButton jRadioButton1080;
    private javax.swing.JRadioButton jRadioButton480;
    private javax.swing.JRadioButton jRadioButton720;
    private javax.swing.JRadioButton jRadioButtonAVI;
    private javax.swing.JRadioButton jRadioButtonHQ;
    private javax.swing.JRadioButton jRadioButtonHigh;
    private javax.swing.JRadioButton jRadioButtonMP3;
    private javax.swing.JRadioButton jRadioMP4;
    private javax.swing.JScrollPane jScrollPaneConsole;
    private javax.swing.JTextArea jTextAreaConsole;
    private javax.swing.JTextField jTextFieldUrl;
    private com.gomez.component.MediaPoller mediaPoller1;
    // End of variables declaration//GEN-END:variables
}
