//Import necessary libraries
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.ui.FlatLineBorder;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import static constants.RegexConstants.*;
import static constants.DatabaseConstants.*;
import static constants.Colors.*;
import static features.SoundPlayer.*;
import features.ForgotPassword;

public class Login extends JFrame implements KeyListener{
    
    //Components initialization
    private JPanel contentPanel;
    private final JPanel signupP, loginP;
    private final JLabel sLabel, lLabel, sLabelLink, lLabelLink, forgotPasswordLink, qrCodeLoginLink, lErrorLabel, sErrorLabel;
    private final JTextField sUser, sEmail;
    private final JPasswordField sPass;
    public final JTextField lUser;
    public final JPasswordField lPass;
    private static int loginAttempt = 0;
    private static long lockTime = 0;
    private static JButton sButton;
    public static JButton lButton;

    //Login constructor
    public Login() {
        setTitle("Log in");
        loginSound();

        // -- GUI creation and designs --
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setSize(700, 300);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Sign up part
        signupP = new JPanel(new GridLayout(7, 1, 0, 22));
        signupP.setSize(352, 612);
        signupP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        sLabel = new JLabel("Sign Up", JLabel.CENTER);
        sLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        sLabel.setForeground(UIManager.getColor("Label.foreground"));

        sUser = new JTextField(20);
        sUser.addKeyListener(this);
        sUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        sUser.setToolTipText("Username");
        sUser.setCaretColor(accentColor);

        sEmail = new JTextField(20);
        sEmail.addKeyListener(this);
        sEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        sEmail.setToolTipText("Email");
        sEmail.setCaretColor(accentColor);

        sPass = new JPasswordField();
        sPass.addKeyListener(this);
        sPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        sPass.setToolTipText("Password");
        sPass.setCaretColor(accentColor);

        textFieldDesigner(sUser);
        textFieldDesigner(sEmail);
        passwordFieldDesigner(sPass);

        sButton = new JButton("Sign Up");
        sButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sButton.setForeground(accentColor);
        sButton.setBackground(backgroundColor);
        buttonDesigner(sButton);
        sButton.setFocusable(false);
        sButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sButton.setBackground(accentColor);
                sButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sButton.setBackground(backgroundColor);
                sButton.setForeground(accentColor);
            }

        });
        sButton.addActionListener(e -> new Thread(this::handleSignUp).start());

        sErrorLabel = new JLabel();
        sErrorLabel.setText("");
        URL errorPath = getClass().getResource("/icons/error.png");
        if (errorPath != null) {
            ImageIcon errorIcon = new ImageIcon(errorPath);
            sErrorLabel.setIcon(errorIcon);
        } else {
            errorSound();
            System.err.println("Error: Resource not found: /icons/error.png");
        }
        sErrorLabel.setOpaque(true);
        sErrorLabel.setForeground(new Color(255, 255, 255));
        sErrorLabel.setBackground(errorColor);
        sErrorLabel.setBorder(new FlatLineBorder(new Insets(5, 10, 5, 10), Color.red, 1f, 12));
        sErrorLabel.setVisible(false);

        sLabelLink = new JLabel("Already have an account? Log in here!");
        sLabelLink.setForeground(Color.WHITE);
        sLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Log In");
                    loginSound();
                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");
                    sErrorLabel.setVisible(false);
                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    errorSound();
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sLabelLink.setForeground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sLabelLink.setForeground(Color.WHITE);
            }
        });

        signupP.add(sLabel);
        signupP.add(sUser);
        signupP.add(sEmail);
        signupP.add(sPass);
        signupP.add(sButton);
        signupP.add(sErrorLabel);
        signupP.add(sLabelLink);

        //Login part
        loginP = new JPanel(new GridLayout(8, 1,30,15));
        loginP.setSize(300, 500);
        loginP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lLabel = new JLabel("Log In", JLabel.CENTER);
        lLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        lLabel.setForeground(UIManager.getColor("Label.foreground"));

        lUser = new JTextField(20);
        lUser.addKeyListener(this);
        lUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username or Email");
        lUser.setToolTipText("Username");
        lUser.setCaretColor(accentColor);

        lPass = new JPasswordField();
        lPass.addKeyListener(this);
        lPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        lPass.setToolTipText("Password");
        lPass.setCaretColor(accentColor);

        textFieldDesigner(lUser);
        passwordFieldDesigner(lPass);

        lButton = new JButton("Log In");
        lButton.setForeground(accentColor);
        lButton.setBackground(backgroundColor);
        buttonDesigner(lButton);
        lButton.setFocusable(false);
        lButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lButton.setBackground(accentColor);
                lButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lButton.setBackground(backgroundColor);
                lButton.setForeground(accentColor);
            }
        });
        lButton.addActionListener(e -> new Thread(this::handleLogin).start());

        lErrorLabel = new JLabel();
        lErrorLabel.setText("");
        if (errorPath != null) {
            ImageIcon errorIcon = new ImageIcon(errorPath);
            lErrorLabel.setIcon(errorIcon);
        } else {
            errorSound();
            System.err.println("Error: Resource not found: /icons/error.png");
        }
        lErrorLabel.setOpaque(true);
        lErrorLabel.setForeground(new Color(255, 255, 255));
        lErrorLabel.setBackground(errorColor);
        lErrorLabel.setBorder(new FlatLineBorder(new Insets(5, 10, 5, 10), Color.red, 1f, 12));
        lErrorLabel.setVisible(false);

        lLabelLink = new JLabel("Don't have an account? Sign up here!");
        lLabelLink.setForeground(Color.WHITE);
        lLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Sign Up");
                    loginSound();
                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");
                    lErrorLabel.setVisible(false);
                    remove(contentPanel);
                    contentPanel = signupP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    errorSound();
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lLabelLink.setForeground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lLabelLink.setForeground(Color.WHITE);
            }
        });

        forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setForeground(Color.WHITE);
        forgotPasswordLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPassword forgotPass = new ForgotPassword(DB_URL, DB_USER, DB_PASSWORD);
                forgotPass.forgotFunc();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLink.setForeground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLink.setForeground(Color.WHITE);
            }
        });

        qrCodeLoginLink = new JLabel("Log in using QR Code");
        qrCodeLoginLink.setForeground(Color.WHITE);
        qrCodeLoginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        qrCodeLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new QRCodeLogin(Login.this);
                loginSound();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                qrCodeLoginLink.setForeground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                qrCodeLoginLink.setForeground(Color.WHITE);
            }
        });

        loginP.add(lLabel);
        loginP.add(lUser);
        loginP.add(lPass);
        loginP.add(lButton);
        loginP.add(lErrorLabel);
        loginP.add(lLabelLink);
        loginP.add(forgotPasswordLink);
        loginP.add(qrCodeLoginLink);

        URL iconURL = getClass().getResource("/icons/Favicon.png");
        ImageIcon icon;
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        } else {
            errorSound();
            JOptionPane.showMessageDialog(null, "Icon not found", "Error", JOptionPane.ERROR_MESSAGE);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addKeyListener(this);
        setLayout(new BorderLayout());
        contentPanel = loginP;
        add(contentPanel, BorderLayout.CENTER);
        setResizable(false);
        setSize(352, 612);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //user registration and login handling
    private void handleSignUp() {
        //Get inputs from fields
        char[] toCheck = sPass.getPassword();
        String userF = sUser.getText();
        String passF = new String(toCheck);
        String emailF = sEmail.getText();

        //Checks empty field
        if (userF.isEmpty() || passF.isEmpty() || emailF.isEmpty()) {
            sErrorLabel.setText("<html><div style='width:156px'>Please enter credentials, Text field cannot be empty.</div></html>");
            errorSound();
            sErrorLabel.setVisible(true);
            clearErrorDisplay();
            return;
        }

        //Username validation
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM Credentials WHERE BINARY Users = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, userF);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username already exists.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                    return;
                } else if (userF.length() < 4) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username length cannot be less than 4 characters.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                    return;
                } else if (userF.length() > 20) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username length cannot exceed 20 characters.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                    return;
                } else if (!userF.matches(COMPLEX_PASSWORD_PATTERN)) {
                    sErrorLabel.setText("<html><div style='width:156px'>Username can only contain letters (A-Z), numbers (0-9), and underscores (_).</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                    return;
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Username error: " + ex.getMessage() , "Username Error", JOptionPane.WARNING_MESSAGE);
            sPass.setText("");
        }

        //Email validation
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM Credentials WHERE BINARY Emails = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, emailF);
                ResultSet resultSet = statement.executeQuery();

                if (!emailF.endsWith("@gmail.com")) {
                    sErrorLabel.setText("<html><div style='width:156px'>For now, We only accept Gmail. Sorry for the inconvenience.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                } else if (resultSet.next()) {
                    sErrorLabel.setText("<html><div style='width:156px'>Email already in use.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                    return;
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Email Error: " + ex.getMessage(), "Email Error", JOptionPane.WARNING_MESSAGE);
        }

        //Password validation
        String passwordToCheck = sPass.getText();

        if (!passwordToCheck.matches(COMPLEX_PASSWORD_PATTERN)) {
            sErrorLabel.setText("<html><div style='width:156px'>Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.</div></html>");
            errorSound();
            sErrorLabel.setVisible(true);
            clearErrorDisplay();
            sPass.setText("");
            return;
        } else if (sPass.getText().length() < 8) {
            sErrorLabel.setText("<html><div style='width:156px'>Password length cannot be less than 8 characters.</div></html>");
            sErrorLabel.setVisible(true);
            clearErrorDisplay();
            errorSound();
            sPass.setText("");
            return;
        } else if (sPass.getText().length() > 20 ) {
            sErrorLabel.setText("<html><div style='width:156px'>Password length cannot exceed 20 characters.</div></html>");
            errorSound();
            sErrorLabel.setVisible(true);
            clearErrorDisplay();
            sPass.setText("");
            return;
        }

        //User registration into database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = "INSERT INTO Credentials (Users, Passwords, Emails, Roles) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, userF);
                statement.setString(2, BCrypt.hashpw(passF, BCrypt.gensalt(14)));  //Hash the pass before storing into database
                statement.setString(3, emailF);
                statement.setString(4, "User");
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    loginSound();
                    JOptionPane.showMessageDialog(contentPanel, "Signup successful!", "Registration successful", JOptionPane.INFORMATION_MESSAGE);
                    sErrorLabel.setVisible(false);
                    setTitle("Log In");
                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    sErrorLabel.setText("<html><div style='width:156px'>Signup failed.</div></html>");
                    errorSound();
                    sErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    sPass.setText("");
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        Arrays.fill(toCheck, '\0');
    }

    private void handleLogin() {
        //Checks if the user is locked out
        if (System.currentTimeMillis() < lockTime) {
            long remainingTime = (lockTime - System.currentTimeMillis()) / 1000;
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Too many failed attempts. Please wait " + remainingTime + " seconds.", "Login Locked", JOptionPane.WARNING_MESSAGE);
            lPass.setText("");
            return;
        }

        //Get inputs from fields
        char[] toCheck = lPass.getPassword();
        String userF = lUser.getText();
        String passF = new String(toCheck);

        //Checks empty field
        if (userF.isEmpty() || passF.isEmpty()) {
            lErrorLabel.setText("<html><div style='width:156px'>Please enter credentials, Text field cannot be empty.</div></html>");
            errorSound();
            lErrorLabel.setVisible(true);
            clearErrorDisplay();
            handleFailedAttempt();
            return;
        }

        boolean isEmail = userF.endsWith("@gmail.com");

        //Checks existing user with correct password
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = isEmail ? "SELECT * FROM Credentials WHERE BINARY Emails = ?" : "SELECT * FROM Credentials WHERE BINARY Users = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setString(1, userF);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Verify the input password and recorded hash
                    if (BCrypt.checkpw(passF, resultSet.getString("Passwords"))) {
                        loginSound();
                        JOptionPane.showMessageDialog(contentPanel, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        //Reset attempts
                        loginAttempt = 0;

                        //After successful login
                        offlineSound();
                        dispose();

                        if (resultSet.getString("Roles").equalsIgnoreCase("Administrator")) {
                            //Admin Control
                            loginSound();
                            new MainPanel(userF);
                        } else if (resultSet.getString("Roles").equalsIgnoreCase("User")) {
                            //Code ni Rommards
                            loginSound();
                            new MainPanel(userF);
                        } else {
                            loginSound();
                            new MainPanel(userF);
                        }

                    } else {
                        lErrorLabel.setText("<html><div style='width:156px'>Password incorrect, try again.</div></html>");
                        errorSound();
                        lErrorLabel.setVisible(true);
                        clearErrorDisplay();
                        lPass.setText("");
                        handleFailedAttempt();
                    }
                } else {
                    lErrorLabel.setText("<html><div style='width:156px'>Username doesn't exist.</div></html>");
                    errorSound();
                    lErrorLabel.setVisible(true);
                    clearErrorDisplay();
                    lPass.setText("");
                    handleFailedAttempt();
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        Arrays.fill(toCheck, '\0');
    }

    //Text Field Designer
    private void textFieldDesigner(JTextField textField) {
        textField.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth: 2; borderColor: #1877F2;");

        textField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        textField.setBackground(backgroundColor);
    }

    //Password Field Designer
    private void passwordFieldDesigner(JPasswordField passwordField) {
        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton: true; showCapsLock: true; borderWidth: 2; borderColor: #1877F2;");

        passwordField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        passwordField.setBackground(backgroundColor);
    }

    //Button Designer
    private void buttonDesigner(JButton button) {
        button.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 2; borderColor: #1877F2;");

        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        button.setBackground(backgroundColor);
    }

    //Handle failed login attempts
    private void handleFailedAttempt() {
        loginAttempt++;
        if (loginAttempt >= 3) {
            lockTime = System.currentTimeMillis() + (2 * 60 * 1000); // 2 minutes lockout
            errorSound();
            JOptionPane.showMessageDialog(contentPanel, "Too many failed attempts. Please wait 2 minutes.", "Login Locked", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Clear the error message on screen
    private void clearErrorDisplay() {
        try {
            Thread.sleep(5000);
            lErrorLabel.setVisible(false);
            sErrorLabel.setVisible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Main method
    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        new Login();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Null function
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Null function
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            if(loginP.isShowing()) {
                lButton.doClick();
            }if(signupP.isShowing()) {
                sButton.doClick();
            }
        }
    }
}

//Call after successful login
class MainPanel extends JFrame{
    public MainPanel(String username) {
        setTitle("Main Panel : @" + username);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();

        JLabel usernameLabel = new JLabel("@" + username, JLabel.CENTER);
        usernameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 18));
        usernameLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to the Main Panel", JLabel.CENTER);
        welcomeLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusable(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Color.ORANGE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(50, 50));
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(e -> {
            offlineSound();
            JOptionPane.showMessageDialog(this, "You are logged out!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login();
        });
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(Color.ORANGE);
                logoutButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(Color.WHITE);
                logoutButton.setForeground(Color.ORANGE);
            }
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(usernameLabel, BorderLayout.NORTH);
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.add(logoutButton, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}