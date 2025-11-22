package com.gomez.yourdownload.view;

import com.gomez.yourdownload.service.ApiClient; // Importa tu cliente API
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.IOException;

/**
 * Panel de Login con UI construida manualmente (sin diseñador).
 */
public class LoginPanel extends JPanel {

    // Componentes del formulario
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;
    
    private JFrame parentFrame; 
    
    // API's URL
    private final String API_URL = "https://dimedianetapi9.azurewebsites.net"; 

    public LoginPanel(JFrame frame) {
        this.parentFrame = frame;
        initComponentsManual();
    }

    private void initComponentsManual() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        formPanel.setPreferredSize(new Dimension(300, 150));

        txtEmail = new JTextField(15);
        txtPassword = new JPasswordField(15);
        chkRemember = new JCheckBox("Remember me in this machine");
        btnLogin = new JButton("Login");
        
        btnLogin.setBackground(new Color(70, 130, 180)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(btnLogin.getFont().deriveFont(java.awt.Font.BOLD));

        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Password:")); formPanel.add(txtPassword);
        formPanel.add(new JLabel("")); formPanel.add(chkRemember);
        formPanel.add(new JLabel("")); formPanel.add(btnLogin);
        
        this.add(formPanel); 
        
        btnLogin.addActionListener(e -> performLogin()); 
        loadRememberedEmail();
    }
    
    private void loadRememberedEmail() {
        Preferences prefs = Preferences.userRoot().node("YourDownloadApp");
        String rememberedEmail = prefs.get("remembered_email", "");
        if (!rememberedEmail.isEmpty()) {
            txtEmail.setText(rememberedEmail);
            chkRemember.setSelected(true);
        }
    }

    private void performLogin() {
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
                ApiClient client = new ApiClient(API_URL);
                String token = client.login(email, password); 

                if (token != null && !token.isEmpty()) {
                    
                    // 1. Guardar token para la sesión actual y futuras
                    Preferences prefs = Preferences.userRoot().node("YourDownloadApp");
                    prefs.put("jwt_token", token);
                    
                    // 2. Gestionar la persistencia del email y token
                    if (chkRemember.isSelected()) {
                        prefs.put("jwt_token", token);
                        prefs.put("remembered_email", email);
                    } else {
                        prefs.remove("jwt_token");
                        prefs.remove("remembered_email");
                    }

                    SwingUtilities.invokeLater(() -> {
                        new MainScreen(token).setVisible(true); // Abrir MainScreen y pasar el token
                        parentFrame.dispose(); 
                    });
                    
                } else {
                    throw new IOException("Token vacío recibido de la API.");
                }

            } catch (Exception ex) {
                //Error de red, credenciales inválidas, etc.
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