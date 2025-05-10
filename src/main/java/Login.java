//Import necessary libraries
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Random;

public class Login extends JFrame implements KeyListener{

    //Components initialization
    private JPanel contentPanel;
    private final JPanel signupP, loginP;
    private final JLabel sLabel, lLabel, sLabelLink, lLabelLink, forgotPasswordLink;
    private final JTextField sUser, sEmail, lUser;
    private final JPasswordField sPass, lPass;
    private final Color accentColor = Color.decode("#1877F2");
    private final Color panelBg = Color.decode("#1C1C1C");
    private static int loginAttempt = 0;
    private static long lockTime = 0;
    private static JButton sButton, lButton;
    private final String uppercasePattern = ".*[A-Z].*";
    private final String lowercasePattern = ".*[a-z].*";
    private final String digitPattern = ".*\\d.*";
    private final String symbolPattern = ".*[~`!@#$%^&*()_,.?/\"':;{}|<>\\[\\]].*";

    //Class constructor
    public Login() {
        setTitle("Log in");

        //Local database connection
        final String url = "jdbc:mysql://localhost:3306/infoman";
        final String user = "root";
        final String password = "";

        //Online database connection - EXPIRED DATABASE
        /*final jdbc:mysql://sql12.freesqldatabase.com:3306/sql12772723
        final String user = "sql12772723";
        final String password = "p9YsNWBkzK";*/

        //GUI creation and designs
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setSize(700, 300);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Sign up part
        signupP = new JPanel(new GridLayout(6, 1, 30, 15));
        signupP.setSize(300, 500);
        signupP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        sLabel = new JLabel("Sign Up", JLabel.CENTER);
        sLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        sLabel.setForeground(UIManager.getColor("Label.foreground"));

        sUser = new JTextField(20);
        sUser.addKeyListener(this);
        sUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        sUser.setToolTipText("Username");
        sUser.setCaretColor(accentColor);

        sPass = new JPasswordField();
        sPass.addKeyListener(this);
        sPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        sPass.setToolTipText("Password");
        sPass.setCaretColor(accentColor);

        sEmail = new JTextField(20);
        sEmail.addKeyListener(this);
        sEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        sEmail.setToolTipText("Email");
        sEmail.setCaretColor(accentColor);

        textFieldDesigner(sUser);
        textFieldDesigner(sEmail);
        passwordFieldDesigner(sPass);

        sButton = new JButton("Sign Up");
        sButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sButton.setForeground(accentColor);
        sButton.setBackground(panelBg);
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
                sButton.setBackground(panelBg);
                sButton.setForeground(accentColor);
            }

        });
        sButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Get inputs from fields
                        char[] toCheck = sPass.getPassword();
                        String userF = sUser.getText();
                        String passF = new String(toCheck);
                        String emailF = sEmail.getText();

                        //Checks empty field
                        if (userF.isEmpty() || passF.isEmpty() || emailF.isEmpty()) {
                            JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials.", "Credentials Missing", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        //Username validation
                        try (Connection connection = DriverManager.getConnection(url, user, password)) {
                            String selectQuery = "SELECT * FROM credentials WHERE BINARY Users = ?";
                            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                                statement.setString(1, userF);
                                ResultSet resultSet = statement.executeQuery();

                                if (resultSet.next()) {
                                    JOptionPane.showMessageDialog(contentPanel, "Username already exists!", "Username Error", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                } else if (userF.length() < 4) {
                                    JOptionPane.showMessageDialog(contentPanel, "Username length must not lower than 4", "Username Error", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                } else if (userF.length() > 20) {
                                    JOptionPane.showMessageDialog(contentPanel, "Username length must not exceed 20", "Username Error", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                } else if (!userF.matches("[a-zA-Z0-9_]+")) {
                                    JOptionPane.showMessageDialog(contentPanel, "Username can only contain letters, numbers, and underscores.", "Username Error", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                }
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(contentPanel, "Username error: " + ex.getMessage() , "Username Error", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                        }

                        //Email validation
                        try (Connection connection = DriverManager.getConnection(url, user, password)) {
                            String selectQuery = "SELECT * FROM credentials WHERE BINARY Emails = ?";
                            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                                statement.setString(1, emailF);
                                ResultSet resultSet = statement.executeQuery();

                                if (!emailF.endsWith("@gmail.com")) {
                                    JOptionPane.showMessageDialog(contentPanel, "We only accept Google mails and commercial businesses TLDs", "Email not accepted", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                } else if (resultSet.next()) {
                                    JOptionPane.showMessageDialog(contentPanel, "Email already used.", "Email used", JOptionPane.WARNING_MESSAGE);
                                    sPass.setText("");
                                    return;
                                }
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(contentPanel, "Email Error: " + ex.getMessage(), "Email Error", JOptionPane.WARNING_MESSAGE);
                        }

                        //Password validation
                        String passwordToCheck = sPass.getText();

                        if (!(passwordToCheck.matches(uppercasePattern) &&
                                passwordToCheck.matches(lowercasePattern) &&
                                passwordToCheck.matches(digitPattern) &&
                                passwordToCheck.matches(symbolPattern))) {
                            JOptionPane.showMessageDialog(contentPanel, "Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.", "Password Error", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        } else if (sPass.getText().length() < 8) {
                            JOptionPane.showMessageDialog(contentPanel, "Password length must not lower than 8", "Password Error", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        } else if (sPass.getText().length() > 20 ) {
                            JOptionPane.showMessageDialog(contentPanel, "Password length must not exceed 20", "Password Error", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        }

                        //User registration into database
                        try (Connection connection = DriverManager.getConnection(url, user, password)) {
                            String insertQuery = "INSERT INTO credentials (Users, Passwords, Emails, Roles) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                                statement.setString(1, userF);
                                statement.setString(2, BCrypt.hashpw(passF, BCrypt.gensalt(14)));  //Hash the pass before storing into database
                                statement.setString(3, emailF);
                                statement.setString(4, "User");
                                int rowsInserted = statement.executeUpdate();

                                if (rowsInserted > 0) {
                                    JOptionPane.showMessageDialog(contentPanel, "Signup successful!", "Registration successful", JOptionPane.INFORMATION_MESSAGE);
                                    setTitle("Log In");
                                    remove(contentPanel);
                                    contentPanel = loginP;
                                    add(contentPanel, BorderLayout.CENTER);
                                    revalidate();
                                    repaint();
                                } else {
                                    JOptionPane.showMessageDialog(contentPanel, "Signup failed.", "Signup Error", JOptionPane.ERROR_MESSAGE);
                                    sPass.setText("");
                                }
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                        Arrays.fill(toCheck, '\0');
                    }
                }).start();
            }
        });

        sLabelLink = new JLabel("Already have an account? Log in here!");
        sLabelLink.setForeground(Color.WHITE);
        sLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Log In");

                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");

                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
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
        signupP.add(sLabelLink);

        //Login part
        loginP = new JPanel(new GridLayout(6, 1,30,15));
        loginP.setSize(300, 500);
        loginP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lLabel = new JLabel("Log In", JLabel.CENTER);
        lLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        lLabel.setForeground(UIManager.getColor("Label.foreground"));

        lUser = new JTextField(20);
        lUser.addKeyListener(this);
        lUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
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
        lButton.setBackground(panelBg);
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
                lButton.setBackground(panelBg);
                lButton.setForeground(accentColor);
            }
        });
        lButton.addActionListener(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Checks if the user is locked out
                    if (System.currentTimeMillis() < lockTime) {
                        long remainingTime = (lockTime - System.currentTimeMillis()) / 1000;
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
                        JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials.", "Credentials cannot be empty", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //Checks existing user with correct password
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        String selectQuery = "SELECT * FROM credentials WHERE BINARY Users = ?";
                        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                            statement.setString(1, userF);
                            ResultSet resultSet = statement.executeQuery();

                            if (resultSet.next()) {
                                String hashedPass = resultSet.getString("Passwords");

                                // Verify the password and recorded hash
                                if (BCrypt.checkpw(passF, hashedPass)) {
                                    JOptionPane.showMessageDialog(contentPanel, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                                    //Reset attempts
                                    loginAttempt = 0;

                                    //After successful login
                                    dispose();

                                    if (resultSet.getString("Roles").equalsIgnoreCase("Admin")) {
                                        //Admin Control
                                        new MainPanel(userF);
                                    } else if (resultSet.getString("Roles").equalsIgnoreCase("User")) {
                                        //Code ni Rommards
                                        new MainPanel(userF);
                                    } else {
                                        new MainPanel(userF);
                                    }

                                } else {
                                    JOptionPane.showMessageDialog(contentPanel, "Incorrect password.", "Password incorrect", JOptionPane.WARNING_MESSAGE);
                                    lPass.setText("");
                                    handleFailedAttempt();
                                }
                            } else {
                                JOptionPane.showMessageDialog(contentPanel, "Username doesn't exist.", "Username cannot found", JOptionPane.WARNING_MESSAGE);
                                lPass.setText("");
                                handleFailedAttempt();
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                    Arrays.fill(toCheck, '\0');
                }
            }).start();
        });

        lLabelLink = new JLabel("Don't have an account? Sign up here!");
        lLabelLink.setForeground(Color.WHITE);
        lLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Sign Up");

                    sUser.setText("");
                    sPass.setText("");
                    sEmail.setText("");
                    lUser.setText("");
                    lPass.setText("");

                    remove(contentPanel);
                    contentPanel = signupP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
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
                String email = JOptionPane.showInputDialog(contentPanel, "Enter your gmail:", "Forgot Password", JOptionPane.QUESTION_MESSAGE);
                String subject, message;
                if (email != null && !email.isEmpty()) {
                    int[] generatedCode = new int[4];
                    for (int i = 0; i < 4; ++i) {
                        generatedCode[i] = new Random().nextInt(10);
                    }

                    StringBuilder code = new StringBuilder();
                    for (int n : generatedCode) {
                        code.append(n);
                    }

                    subject = "Code Verification - Connect";
                    message = "Your verification code is: " + code;

                    EmailSender.sendEmail(email, subject, message);
                    JOptionPane.showMessageDialog(null, "Verification code sent to your email.", "Email Sent", JOptionPane.INFORMATION_MESSAGE);

                    String verifyCode = JOptionPane.showInputDialog(contentPanel, "Enter the code:", "Verify Code", JOptionPane.INFORMATION_MESSAGE);
                    if (code.toString().equals(verifyCode)) {
                        String newPassword = JOptionPane.showInputDialog(contentPanel, "Enter your new password:", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
                        String retypePassword = JOptionPane.showInputDialog(contentPanel, "Retype your new password:", "Reset Password", JOptionPane.INFORMATION_MESSAGE);

                        if (newPassword.equals(retypePassword)) {
                            if (!(newPassword.matches(uppercasePattern) &&
                                    newPassword.matches(lowercasePattern) &&
                                    newPassword.matches(digitPattern) &&
                                    newPassword.matches(symbolPattern))) {
                                JOptionPane.showMessageDialog(contentPanel, "Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.", "Password Error", JOptionPane.WARNING_MESSAGE);
                            } else if (newPassword.length() < 8 || newPassword.length() > 20) {
                                JOptionPane.showMessageDialog(contentPanel, "Password length must be between 8 and 20 characters.", "Password Error", JOptionPane.WARNING_MESSAGE);
                            } else {
                                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                                    String updateQuery = "UPDATE credentials SET Passwords = ? WHERE BINARY Emails = ?";
                                    try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                                        statement.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(14)));
                                        statement.setString(2, email);

                                        int rowsUpdated = statement.executeUpdate();
                                        if (rowsUpdated > 0) {
                                            subject = "Reset Password Successful - Connect";
                                            message = "Your password has been reset successfully.";

                                            EmailSender.sendEmail(email, subject,message);

                                            JOptionPane.showMessageDialog(contentPanel, "Password reset successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(contentPanel, "Failed to reset password. Please try again.", "Failed", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(contentPanel, "Password do not match.", "Password Error", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(contentPanel, "Invalid Code");
                    }
                } else {
                    JOptionPane.showMessageDialog(contentPanel, "Email cannot be empty", "Email Null Error", JOptionPane.ERROR_MESSAGE);
                }
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

        loginP.add(lLabel);
        loginP.add(lUser);
        loginP.add(lPass);
        loginP.add(lButton);
        loginP.add(lLabelLink);
        loginP.add(forgotPasswordLink);

//        ImageIcon icon = new ImageIcon("src/main/resources/icons/messengerwhiteflip.png");
        URL iconURL = getClass().getResource("/icons/messengerwhiteflip.png");
        ImageIcon icon = null;
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        setLayout(new BorderLayout());
        contentPanel = loginP;
        add(contentPanel, BorderLayout.CENTER);
        setIconImage(icon.getImage());
        addKeyListener(this);
        setResizable(false);
        setLocation(500, 150);
        setSize(300, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Text Field Designer
    private void textFieldDesigner(JTextField textField) {
        textField.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth: 2; borderColor: #1877F2;");

        textField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        textField.setBackground(panelBg);
    }

    //Password Field Designer
    private void passwordFieldDesigner(JPasswordField passwordField) {
        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton: true; showCapsLock: true; borderWidth: 2; borderColor: #1877F2;");

        passwordField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        passwordField.setBackground(panelBg);
    }

    //Button Designer
    private void buttonDesigner(JButton button) {
        button.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 2; borderColor: #1877F2;");

        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        button.setBackground(panelBg);
    }

    //Handle failed login attempts
    private void handleFailedAttempt() {
        loginAttempt++;
        if (loginAttempt >= 3) {
            lockTime = System.currentTimeMillis() + (2 * 60 * 1000); // 2 minutes lockout
            JOptionPane.showMessageDialog(contentPanel, "Too many failed attempts. Please wait 2 minutes.", "Login Locked", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Main method
    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
//        ModifiedFlatMacDarkLaf.setup();
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