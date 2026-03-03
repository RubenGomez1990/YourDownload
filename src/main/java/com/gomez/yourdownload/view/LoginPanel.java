package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller;
import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.*;
import java.io.IOException;

/**
 * Provides the login interface for the application. Handles user authentication
 * via the MediaPoller component and manages session persistence.
 *
 * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class LoginPanel extends JPanel {

    // Form elements
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;
    private JFrame parentFrame;
    // Poller variable
    private MediaPoller mediaPoller;

    /**
     * Constructs a new LoginPanel.
     *
     * @param frame The parent JFrame used for centering and screen management.
     * @param poller The MediaPoller instance used for authentication.
     */
    public LoginPanel(JFrame frame, MediaPoller poller) {
        this.parentFrame = frame;
        this.mediaPoller = poller;
        initComponentsManual();
    }

    /**
     * Manually initializes the UI components and layout settings. Uses a null
     * layout to match the project's design requirements.
     */
    public void initComponentsManual() {
        //  Null layout for the entire project
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        int loginWidth = 400;
        int loginHeight = 300;

        // Container panel for the form
        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(null);
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        whiteBox.setBounds(50, 50, loginWidth, loginHeight);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, 40, 80, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(120, 40, 300, 30);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 90, 80, 25);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 90, 300, 25);

        chkRemember = new JCheckBox("Remember me in this machine");
        chkRemember.setBounds(140, 140, 250, 25);
        chkRemember.setBackground(Color.WHITE);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 180, 300, 40); // Botón más grande para usabilidad
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        this.add(lblEmail);
        this.add(txtEmail);
        this.add(lblPass);
        this.add(txtPassword);
        this.add(chkRemember);
        this.add(btnLogin);

        btnLogin.addActionListener(e -> performLogin());
        loadRememberedEmail();
        this.setPreferredSize(new Dimension(500, 400));
    }
   
    /**
     * Retrieves the remembered email address from user preferences if available.
     */
    public void loadRememberedEmail() {
        Preferences prefs = Preferences.userRoot().node("YourDownloadApp");
        String rememberedEmail = prefs.get("remembered_email", "");
        if (!rememberedEmail.isEmpty()) {
            txtEmail.setText(rememberedEmail);
            chkRemember.setSelected(true);
        }
    }
    
    /**
     * Executes the authentication logic in a background thread.
     * Validates credentials, updates visual feedback, and notifies the parent frame on success.
     */
    public void performLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Connecting...");

        new Thread(() -> {
            try {
                // we use the component instead of ApiClient
                String token = this.mediaPoller.login(email, password);

                if (token != null && !token.isEmpty()) {

                    // we configurate the token
                    this.mediaPoller.setToken(token);

                    // save preferences
                    Preferences prefs = Preferences.userRoot().node("YourDownloadApp");
                    prefs.put("jwt_token", token);

                    if (chkRemember.isSelected()) {
                        prefs.put("remembered_email", email);
                    } else {
                        prefs.remove("remembered_email");
                    }

                    SwingUtilities.invokeLater(() -> {
                        if (parentFrame instanceof MainScreen) {
                            // Option A: main window, we tell it to change the panel
                            ((MainScreen) parentFrame).loginSuccessful(token);
                        } else {
                            // Option B: if is opened outside, we integrate it
                            MainScreen ms = new MainScreen(token, this.mediaPoller);
                            ms.setVisible(true);
                            if (parentFrame != null) {
                                parentFrame.dispose();
                            }
                        }
                    });

                } else {
                    throw new IOException("Empty token from api");
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Login Failed: " + ex.getMessage() + "\nCheck credentials.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText("");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");
                });
            }
        }).start();
    }
}
