package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller; // IMPORTANTE: Importamos el componente
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.IOException;

public class LoginPanel extends JPanel {

    // Componentes del formulario
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;
    
    private JFrame parentFrame; 
    
    // VARIABLE PARA EL POLLER
    private MediaPoller mediaPoller; 

    // CONSTRUCTOR MODIFICADO (Aquí es donde daba el error)
    // Ahora acepta (JFrame frame, MediaPoller poller)
    public LoginPanel(JFrame frame, MediaPoller poller) {
        this.parentFrame = frame;
        this.mediaPoller = poller; // Guardamos la referencia que nos pasa MainScreen
        initComponentsManual();
    }

    private void initComponentsManual() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        formPanel.setPreferredSize(new Dimension(420, 200));

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
                // USAMOS EL COMPONENTE (mediaPoller.login) EN VEZ DE ApiClient
                String token = this.mediaPoller.login(email, password); 

                if (token != null && !token.isEmpty()) {
                    
                    // Configuramos el token en el componente único
                    this.mediaPoller.setToken(token);

                    // Guardar preferencias
                    Preferences prefs = Preferences.userRoot().node("YourDownloadApp");
                    prefs.put("jwt_token", token);
                    
                    if (chkRemember.isSelected()) {
                        prefs.put("remembered_email", email);
                    } else {
                        prefs.remove("remembered_email");
                    }

                    SwingUtilities.invokeLater(() -> {
                        // Pasamos el componente YA CONFIGURADO al MainScreen
                        new MainScreen(token, this.mediaPoller).setVisible(true); 
                        parentFrame.dispose(); 
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