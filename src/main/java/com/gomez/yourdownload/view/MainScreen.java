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
 * Main application windows for YourDownload
 * Manages the user interface for downloading media, configure quality settings
 * and synchronizing with the server.
 * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class MainScreen extends javax.swing.JFrame {

    /** 
     * Path where download files will be stored 
     */
    private String destinyPath = "";
    /** 
     * Reference to the originalPanel 
     */
    private JPanel originalPanel;
    /** 
     * List that contains the downloads 
     */
    private List<DownloadInfo> resourcesList;
    /** 
     * Token for the login 
     */
    private String jwtToken;
    /** 
     * Identifier for the current active view (e.g., "MAIN", "LIBRARY"). 
     */
    private String currentView = "MAIN";
    /** 
     * Color for the UI 
     */
    java.awt.Color azulLogin = new java.awt.Color(74, 134, 173);
    
    
    /**
     * Constructs the main application screen.
     * Initializes UI components, styles, and background services.
     * @param token The JWT authentication token.
     * @param pollerInstance The MediaPoller component instance for server sync.
     */
    public MainScreen(String token, MediaPoller pollerInstance) {
        this.jwtToken = token;// Almacenamos el token JWT para usarlo en descargas, etc.
        this.mediaPoller1 = pollerInstance;

        // UI creation
        resourcesList = DownloadService.loadHistory();
        initComponents();
        originalPanel = (JPanel) getContentPane();
        this.setResizable(false);
        
        
        // Style settings buttons
        jButtonDownload.setToolTipText("Start downloading the video/audio");
        jButtonDownload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        try {
            jButtonDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/download_white.png")));
            jButtonDownload.setText("Download");
        } catch (Exception e) {
            System.err.println("Icon not found");
        }

        jButtonDownload.setBackground(azulLogin);
        jButtonDownload.setForeground(java.awt.Color.WHITE);
        jButtonDownload.setContentAreaFilled(true);
        jButtonDownload.setOpaque(true);
        jButtonDownload.putClientProperty("JButton.buttonType", "square");
        jButtonDownload.putClientProperty("JComponent.arc", 0);
        
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

        jProgressBar.setStringPainted(true);
        jProgressBar.setForeground(azulLogin);
        jProgressBar.setBackground(new java.awt.Color(235, 235, 235));
        jProgressBar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        jProgressBar.putClientProperty("JComponent.arc", 0);
        jProgressBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(210, 210, 210), 1));
        jLabelDownloadStatus.setBounds(120, 240, 670, 25);
        jLabelDownloadStatus.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabelDownloadStatus.setText(""); // Initializes empty.

        jRadioMP4.setSelected(true);
        setPanelEnabled(jPanelQuality, true);  // Actual video quality
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
            // --- Login Case ---
            showLoginScreen(); // Este método ahora se encargará de encoger la ventana
        } else {
            // --- Main Case ---
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
     * Automatically generated method by NetBeans GUI Builder to initialize forms.
     * @throws Exception If component instantiation fails due to missing classes or resources.
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
    /**
     * Handles the action event for the JTextFieldUrl.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jTextFieldUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUrlActionPerformed
    /**
     * Handles the action event for the JRadioButton1080.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jRadioButton1080ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1080ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1080ActionPerformed
    /**
     * Handles the action event for the jCheckBoxSubtitlesYes.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jCheckBoxSubtitlesYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSubtitlesYesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxSubtitlesYesActionPerformed
    
    /**
     * Opens a directory chooser to set the download destination path.
     * @param evt The action event.
     *
     */
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
    
    /**
     * Closes the application with confirmation and saves history.
     * @param evt The action event.
     */
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
    /**
     * Displays the About dialog with info about the application
     * @param evt The action event triggered by the menu item.
     * @throws RuntimeException If the dialog window fails to initialize properly.
     */
    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        About dialog = new About(this, true); // 'this' es tu JFrame principal
        dialog.setLocationRelativeTo(this); // Centrado respecto a la ventana principal
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed
    /**
     * Navigates to the user Preferences panel.
     * @param evt The action event triggered by the menu item.
     * @throws ClassCastException If the current content pane cannot be cast to a JPanel.
     */
    private void jMenuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreferencesActionPerformed
        javax.swing.JPanel currentPanel = (javax.swing.JPanel) this.getContentPane();
        setContentPane(new PreferencesPanel(this, currentPanel));

        revalidate();
        repaint();
    }//GEN-LAST:event_jMenuItemPreferencesActionPerformed
    /**
     * Initiates the media download process. It uses binaries form yt-dlp.
     * Evaluates users selections (format, quality) to build the command, download files and update progress dynamically.
     * @param evt The action event triggered by the download button.
     * @throws IllegalStateException If the background thread fails to start or gets interrupted unexpectedly.
     */
    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        final String url = jTextFieldUrl.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL");
            return;
        }

        jButtonDownload.setEnabled(false);
        jTextAreaConsole.setText("");
        jProgressBar.setValue(0);
        jProgressBar.setString("0%"); // Force the starting text
        jLabelDownloadStatus.setText("Downloading and converting...");
        jLabelDownloadStatus.setForeground(azulLogin);
        jLabelDownloadStatus.setVisible(true);

        // Determinate format and quality
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
                if (binariesPath.isEmpty() || !new File(binariesPath).exists()) {
                    throw new Exception("The path for 'yt-dlp' is invalid or not set in Preferences.");
                }

                File ytDlpExe = new File(binariesPath);
                // We use an unique identifier for this download
                String timestampId = String.valueOf(System.currentTimeMillis() / 1000);

                List<String> command = new ArrayList<>();
                command.add(ytDlpExe.getAbsolutePath());
                command.add("--ffmpeg-location");
                command.add(ytDlpExe.getParent());

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

                // Name template: title + temporal ID
                String nameTemplate = "%(title)s_" + timestampId + ".%(ext)s";
                File outputFileTemplate = new File(destinyPath, nameTemplate);
                command.add("-o");
                command.add(outputFileTemplate.getAbsolutePath());
                command.add(url);

                Process process = new ProcessBuilder(command).redirectErrorStream(true).start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String outputLine = line;
                        SwingUtilities.invokeLater(() -> {
                            jTextAreaConsole.append(outputLine + "\n");
                            updateProgressBar(outputLine);
                        });
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new Exception("Process finished with error code: " + exitCode);
                }
                process.waitFor();

                // --- KEY: FINAL SEARCH ---
                // Search in the folder every file that contains our ID and our extension.
                File folder = new File(destinyPath);
                File[] matches = folder.listFiles((dir, name) -> name.contains(timestampId) && name.toLowerCase().endsWith("." + targetExt));

                if (matches != null && matches.length > 0) {
                    File finalFile = matches[0];

                    // Registration in the list
                    DownloadInfo info = new DownloadInfo(finalFile.getAbsolutePath(), new Date(), finalFile.length(), targetExt.toUpperCase());
                    resourcesList.add(info);
                    DownloadService.saveHistory(resourcesList);

                    SwingUtilities.invokeLater(() -> {
                        jLabelDownloadStatus.setText("Download Complete!  (" + finalFile.getName() + ")");
                        jLabelDownloadStatus.setForeground(azulLogin);
                        jLabelDownloadStatus.setText("Successfully saved to your library");
                    });
                } else {
                    throw new Exception("File not found after download process.");
                }

            } catch (InterruptedException e) {
                logErrorMain("Download interrupted for URL [" + url + "]: " + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logErrorMain("Download Error for URL [" + url + "]: " + e.getMessage());
            } finally {
                SwingUtilities.invokeLater(() -> jButtonDownload.setEnabled(true));
            }
        }
        ).start();
    }//GEN-LAST:event_jButtonDownloadActionPerformed
    /**
     * Allows the user to select a new folder to save downloaded files.
     * @param evt The action event triggered by the change button.
     * @throws SecurityException If local file system access is restricted.
     */
    private void jButtonChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeActionPerformed
        JFileChooser pathSelector = new JFileChooser();
        pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = pathSelector.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = pathSelector.getSelectedFile();
            destinyPath = folder.getAbsolutePath();

            jLabelSave.setText("Saved at: " + destinyPath);
        }
    }//GEN-LAST:event_jButtonChangeActionPerformed
    /**
     * Triggers the transition from the main screen to the media library view.
     * @param evt The action event triggered by the library button.
     * @throws RuntimeException If the panel replacement process fails.
     */
    private void jButtonLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLibraryActionPerformed
        showLibrary();
    }//GEN-LAST:event_jButtonLibraryActionPerformed
    /**
     * Handles the action event for the JRadioButton720.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jRadioButton720ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton720ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton720ActionPerformed
    /**
     * Handles the action event for the JButtonHigh.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jRadioButtonHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonHighActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonHighActionPerformed
    /**
     * Handles the action event for the JRadioButtonHQ.
     * This method is auto-generated by the designer.
     * Reserved for future implementation 
     */
    private void jRadioButtonHQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonHQActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonHQActionPerformed
    /**
     * Clears session preferences and forces the user to log in again.
     * @param evt The action event triggered by the logout menu item.
     * @throws SecurityException If the application does not have permission to access user preferences.
     */
    private void jMenuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLogoutActionPerformed
        // 1. Deletes the persistence for the session
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("YourDownloadApp");
        prefs.remove("jwt_token");
        prefs.remove("email");

        // 2. Instead of closing, we change the panel
        showLoginScreen();
    }//GEN-LAST:event_jMenuItemLogoutActionPerformed
    /**
     * Toggles the visibility of the text console for viewing detailed download logs.
     * Resizes the application window.
     * @param evt The action event triggered by the log button.
     * @throws IllegalArgumentException If the set bounds are invalid for the window size.
     */
    private void jButtonShowLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowLogActionPerformed

        if (!jPanelConsole.isVisible()) {
            // DETAILED MODE: Show console
            jPanelConsole.setVisible(true);
            this.setSize(1024, 800);
            jButtonShowLog.setText("Hide");
        } else {
            // COMPACT MODE: hide console
            jPanelConsole.setVisible(false);
            this.setSize(1024, 330);
            jButtonShowLog.setText("Log");
        }

        // REFRESH THE UI
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_jButtonShowLogActionPerformed
    /**
     * Mouse listener for the polling status icon to manually toggle the syncing process.
     * @param evt The mouse event triggered by clicking the status icon.
     * @throws NullPointerException If the poller instance is null when clicked.
     */
    private void jLabelStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelStatusMouseClicked
        togglePolling();// TODO add your handling code here:
    }//GEN-LAST:event_jLabelStatusMouseClicked
    /**
     * Updates the UI state when MP4 video format is selected.
     * @param evt The action event.
     * @throws IllegalStateException If the panel state cannot be modified.
     */
    private void jRadioMP4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioMP4ActionPerformed
        // Re-activates the quality video panel
        setPanelEnabled(jPanelQuality, true);

        // Hides the audio panel
        jPanelAudioQuality.setVisible(false);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioMP4ActionPerformed
    /**
     * Updates the UI state when MP3 audio format is selected, disabling video resolutions.
     * @param evt The action event.
     * @throws IllegalStateException If the panel state cannot be modified.
     */
    private void jRadioButtonMP3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMP3ActionPerformed
        setPanelEnabled(jPanelQuality, false);

        // Shows the audio panel (at the side)
        jPanelAudioQuality.setVisible(true);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioButtonMP3ActionPerformed
    /**
     * Updates the UI state when AVI video format is selected.
     * @param evt The action event.
     * @throws IllegalStateException If the panel state cannot be modified.
     */
    private void jRadioButtonAVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonAVIActionPerformed
        // Re-activate the quality video panel
        setPanelEnabled(jPanelQuality, true);

        // Hides the audio panel
        jPanelAudioQuality.setVisible(false);

        revalidate();
        repaint();
    }//GEN-LAST:event_jRadioButtonAVIActionPerformed

    /**
     * The main entry point of the application. Configures the UI theme and instantiates the application.
     * @param args The command line arguments passed during execution.
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
    /**
     * Extracts progress percentages from yt-dlp console output and updates the UI progress bar.
     * @param line A single line of standard output from the download process.
     * @throws NumberFormatException If the regex successfully finds a pattern but parsing as Double fails.
     */
    public void updateProgressBar(String line) {
        Pattern pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)%");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            try {
                double percent = Double.parseDouble(matcher.group(1));
                int intPercent = (int) percent;

                jProgressBar.setValue(intPercent);
                // This secures that text inside the bar is updated
                jProgressBar.setString(intPercent + "%");

            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * Determines the file format extension for classification in the library.
     * @param fileName The name of the file to inspect.
     * @return The uppercase extension (e.g., "MP3"), or "FILE" if no extension exists.
     * @throws NullPointerException If operations attempt to process a null string improperly (handled implicitly).
     */
    public String obtainMimeSimple(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "FILE";
        }
        // We extract everything after the dot and pass it to capital letters
        // This secure that if file is .mp3, the format is "MP3"
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }
   
    /**
     * Locates the yt-dlp executable path configured by the user or attempts to find it in common directories.
     * @return The path string pointing to the executable, or an empty string if not found.
     * @throws SecurityException If the environment restricts reading local app data or system environment variables.
     */
    public String getBinariesPath() {
        Preferences prefs = Preferences.userRoot().node("PreferencesPanel");
        final String PREFS_KEY = "binariesPath";

        String storedPath = prefs.get(PREFS_KEY, "");
        if (!storedPath.isEmpty() && new File(storedPath).exists()) {
            return storedPath;
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
    /**
     * Swaps the interface content pane from the main downloader view to the Media Library view.
     * @throws RuntimeException If instantiating the MediaLibrary panel encounters an error.
     */
    public void showLibrary() {
        this.currentView = "LIBRARY"; // We save the library position
        this.setSize(1200, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        MediaLibrary libraryPanel = new MediaLibrary(this, originalPanel, resourcesList, this.mediaPoller1);
        setContentPane(libraryPanel);
        revalidate();
        repaint();
    }
    
    /**
     * Instantiates and starts the MediaPoller listener to receive new file updates from the server.
     * @param token The session JWT token to authenticate polling requests.
     * @throws NullPointerException If the component instance is null, printing an error to the console.
     */
    public void initMediaPoller(String token) {
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
    /**
     * Processes events triggered by the MediaPoller when new server files are discovered.
     * Filters duplicates and prompts the user to download new remote media.
     * @param event The polling event containing the collection of newly found media models.
     * @throws SecurityException If local writing permissions prevent downloading the actual files.
     */
    public void handleNewFilesFound(final com.gomez.component.NewMediaEvent event) {
        //Only files that aren't at local library
        List<com.gomez.model.Media> trulyNewFiles = new ArrayList<>();

        for (com.gomez.model.Media remoteFile : event.getNewFiles()) {
            // Check if the files is already in the list
            boolean alreadyExists = resourcesList.stream().anyMatch(local
                    -> (local.getNetworkId() != null && local.getNetworkId().equals(remoteFile.id))
                    || (local.getFileName() != null && local.getFileName().equalsIgnoreCase(remoteFile.mediaFileName))
            );

            if (!alreadyExists) {
                trulyNewFiles.add(remoteFile);
            }
        }

        if (trulyNewFiles.isEmpty()) {
            return;
        }

        //REAL-TIME DETECTION LOG
        int newCount = trulyNewFiles.size();
        String firstFileName = trulyNewFiles.get(0).mediaFileName;
        System.out.println("Poller (Real-time): detected [" + newCount + "] new files since last check.");

        // Interface alert about new files
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
                    // Download
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
    /**
     * Renders the login screen interface by clearing menu bars and replacing the content pane.
     * @throws IllegalStateException If the JFrame state prevents updating the content pane.
     */
    public void showLoginScreen() {
        this.setJMenuBar(null);
        this.setResizable(false);
        LoginPanel login = new LoginPanel(this, this.mediaPoller1);
        this.setContentPane(login);
        this.setSize(500, 320);
        this.setLocationRelativeTo(null);
        this.revalidate();
        this.repaint();
    }
    /**
     * Handler invoked when the user successfully authenticates.
     * Re-initializes the application view and starts synchronization polling.
     * @param token The newly retrieved JWT authentication token.
     * @throws IllegalStateException If restoring the original UI panel fails.
     */
    public void loginSuccessful(String token) { 
        this.jwtToken = token;

        //Restores the original design
        this.setJMenuBar(jMenuBar);
        this.setContentPane(originalPanel);
        jPanelConsole.setVisible(false);
        this.setSize(1024, 330);
        this.setLocationRelativeTo(null);
        this.revalidate();
        this.repaint();
        initMediaPoller(token);
    }
    /**
     * Pauses or resumes the MediaPoller checking thread.
     * @throws NullPointerException If the component reference is inaccessible during the toggle.
     */
    public void togglePolling() {
        boolean running = this.mediaPoller1.isRunning();

        boolean newState = !running;
        this.mediaPoller1.setRunning(newState);

        updateIconState(newState);

        System.out.println("Poller changed to: " + (newState ? "On" : "Off"));
    }
    /**
     * Swaps the visual status icon based on the poller's active state.
     * @param on true if polling is actively running, false otherwise.
     * @throws IllegalArgumentException If the resource path provided for the icons is structurally invalid.
     */
    public void updateIconState(boolean on) {
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
    /**
     * Enables or disables all interactive components within a parent container.
     * @param panel The JPanel containing the components to modify.
     * @param isEnabled State flag to dictate boolean enabled status.
     * @throws NullPointerException If the provided panel argument is null.
     */
    public void setPanelEnabled(javax.swing.JPanel panel, boolean isEnabled) {
        panel.setEnabled(isEnabled);
        for (java.awt.Component comp : panel.getComponents()) {
            comp.setEnabled(isEnabled);
        }
    }
    /**
     * Formats a standard JButton to render purely as a clickable icon with no background box.
     * @param btn The standard JButton to transform.
     * @param iconPath Path matching a resource graphic.
     * @param tooltip Informational text shown on mouse hover.
     * @throws NullPointerException If the button parameter provided is missing.
     */
    public void makeIconOnlyButton(javax.swing.JButton btn, String iconPath, String tooltip) {
        btn.setText("");
        btn.setToolTipText(tooltip);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

       
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        
        try {
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                btn.setIcon(new javax.swing.ImageIcon(imgURL));
            }
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
    }
    /**
     * Helper method to output major errors
     * @param message Detailed description of the error event.
     * @throws IllegalStateException If attempting to execute GUI updates from a detached thread fails.
     */
    public void logErrorMain(String message) {
        // 1. Guardamos el error físicamente en el archivo .log
        writeLog("YT-DLP ERROR", message);

        // 2. Mantenemos tu código original para la interfaz visual
        SwingUtilities.invokeLater(() -> {
            jTextAreaConsole.append("\n[FATAL ERROR] " + message + "\n");
            jLabelDownloadStatus.setText("Error. Check the Log.");
            jLabelDownloadStatus.setForeground(java.awt.Color.RED);
            JOptionPane.showMessageDialog(this, message, "System Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    /**
     * Writes formatted error details to a local project text file for debugging.
     * @param errorType Short tag indicating the scope of the error (e.g. "ERROR 404").
     * @param details Full text describing what caused the malfunction.
     * @throws SecurityException If the local JVM policy blocks the creation or editing of text files.
     */
    public void writeLog(String errorType, String details) {
        try {
            // Usamos el mismo archivo para tener todos los errores juntos
            java.io.File logFile = new java.io.File("yourdownload_errors.log"); 
            java.io.FileWriter fw = new java.io.FileWriter(logFile, true); 
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            java.io.PrintWriter out = new java.io.PrintWriter(bw);
            
            String timeStamp = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date());
            
            out.println("[" + timeStamp + "] [" + errorType + "] " + details);
            out.close();
            
        } catch (Exception e) {
            System.err.println("Critical Error: Could not write to log file. " + e.getMessage());
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
