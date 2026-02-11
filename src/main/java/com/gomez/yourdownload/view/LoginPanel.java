package com.gomez.yourdownload.view;

import com.gomez.component.MediaPoller; // IMPORTANTE: Importamos el componente
import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.*;
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
        // Mantenemos el Layout Nulo que usas en todo el proyecto
        this.setLayout(null);
        this.setBackground(Color.WHITE); // Un tono gris azulado profesional

        // 1. Definimos las dimensiones de nuestro "cuadro de login"
        int loginWidth = 400;
        int loginHeight = 300;

        // 2. Creamos un panel contenedor para el formulario (el cuadrito blanco)
        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(null); // También nulo para posicionar etiquetas y campos
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // IMPORTANTE: Lo centramos respecto a la ventana de 1024x800
        // x = (1024 / 2) - (450 / 2) = 287
        // y = (800 / 2) - (300 / 2) = 250
        whiteBox.setBounds(50, 50, loginWidth, loginHeight);

        // 3. Posicionamos los componentes DENTRO del cuadro blanco (Coordenadas relativas al cuadro)
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
                        if (parentFrame instanceof MainScreen) {
                            // Opción A: Es la ventana principal, le decimos que cambie de panel
                            ((MainScreen) parentFrame).loginSuccessful(token);
                        } else {
                            // Opción B: Si por error se abrió fuera, lo integramos
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
