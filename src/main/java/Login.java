import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Arrays;

public class Login extends JFrame {
    private JPanel contentPanel;
    private final JPanel signupP;
    private final JPanel loginP;
    private final Color accentColor = Color.decode("#1877F2");
    private final Color panelBg = UIManager.getColor("Panel.background");

    public Login() {
        setTitle("Log in");

        //Local database connection
        final String url = "jdbc:mysql://localhost:3306/infoman";
        final String user = "root";
        final String password = "";

        //Online database connection
        /*final jdbc:mysql://sql12.freesqldatabase.com:3306/sql12772723
        final String user = "sql12772723";
        final String password = "p9YsNWBkzK";*/

        contentPanel = new JPanel();
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setSize(700, 300);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //########################## S I G N U P C O D E S ######################################

        signupP = new JPanel(new GridLayout(6, 1, 30, 15));
        signupP.setSize(300, 500);
        signupP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel sLabel = new JLabel("Sign Up", JLabel.CENTER);
        sLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        sLabel.setForeground(UIManager.getColor("Label.foreground"));

        JTextField sUser = new JTextField(20);
        sUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        sUser.setToolTipText("Username");
        sUser.setCaretColor(accentColor);

        JPasswordField sPass = new JPasswordField();
        sPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        sPass.setToolTipText("Password");
        sPass.setCaretColor(accentColor);

        JTextField sEmail = new JTextField(20);
        sEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        sEmail.setToolTipText("Email");
        sEmail.setCaretColor(accentColor);

        textFieldDesigner(sUser);
        textFieldDesigner(sEmail);
        passwordFieldDesigner(sPass);

        JButton sButton = new JButton("Sign Up");
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
                char[] toCheck = sPass.getPassword();
                String userF = sUser.getText();
                String passF = new String(toCheck);
                String emailF = sEmail.getText();

                if (userF.isEmpty() || passF.isEmpty() || emailF.isEmpty()) {
                    JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials.", "Credentials Missing!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String selectQuery = "SELECT * FROM credentials WHERE BINARY Users = ?";
                    try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                        statement.setString(1, userF);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(contentPanel, "Username already exists!", "Username Error!", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(contentPanel, "Username already exists!", "Username Error!", JOptionPane.WARNING_MESSAGE);
                    sPass.setText("");
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String selectQuery = "SELECT * FROM credentials WHERE BINARY Email = ?";
                    try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                        statement.setString(1, emailF);
                        ResultSet resultSet = statement.executeQuery();

                        if(!emailF.contains("@")) {
                            JOptionPane.showMessageDialog(contentPanel, "Email must contain '@'", "Email Error!", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        }

                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(contentPanel, "Email already used!", "Email Error!", JOptionPane.WARNING_MESSAGE);
                            sPass.setText("");
                            return;
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(contentPanel, "Email already used!", "Email Error!", JOptionPane.WARNING_MESSAGE);
                }

                //Password validation
                String uppercasePattern = ".*[A-Z].*";
                String lowercasePattern = ".*[a-z].*";
                String digitPattern = ".*\\d.*";
                String symbolPattern = ".*[~`!@#$%^&*()_,.?/\"':;{}|<>\\[\\]].*";
                @SuppressWarnings("deprecation") String passwordToCheck = sPass.getText();

                if (!(passwordToCheck.matches(uppercasePattern) &&
                        passwordToCheck.matches(lowercasePattern) &&
                        passwordToCheck.matches(digitPattern) &&
                        passwordToCheck.matches(symbolPattern))) {
                    JOptionPane.showMessageDialog(contentPanel, "Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.", "Password Error", JOptionPane.WARNING_MESSAGE);
                    sPass.setText("");
                    return;
                }

                //noinspection deprecation
                if(sPass.getText().length() < 8) {
                    JOptionPane.showMessageDialog(contentPanel, "Password length must not lower than 8!", "Password Error!", JOptionPane.WARNING_MESSAGE);
                    sPass.setText("");
                    return;
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String insertQuery = "INSERT INTO credentials (Users, Password, Email) VALUES (?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                        statement.setString(1, userF);
                        statement.setString(2, passF);
                        statement.setString(3, emailF);
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(contentPanel, "Signup successful!", "Success!", JOptionPane.INFORMATION_MESSAGE);
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
                    JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error.", JOptionPane.ERROR_MESSAGE);
                }
                Arrays.fill(toCheck, '\0');
            }
        });
        JLabel sLabelLink = new JLabel("Already have an account? Log in here!");
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

        //########################## S I G N U P C O D E S ######################################

        //##################################### L O G I N C O D E S ######################################

        loginP = new JPanel(new GridLayout(5, 1,30,15));
        loginP.setSize(300, 500);
        loginP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lLabel = new JLabel("Log In", JLabel.CENTER);
        lLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 20));
        lLabel.setForeground(UIManager.getColor("Label.foreground"));

        JTextField lUser = new JTextField(20);
        lUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        lUser.setToolTipText("Username");
        lUser.setCaretColor(accentColor);

        JPasswordField lPass = new JPasswordField();
        lPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        lPass.setToolTipText("Password");
        lPass.setCaretColor(accentColor);

        textFieldDesigner(lUser);
        passwordFieldDesigner(lPass);

        JButton lButton = new JButton("Log In");
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
            char[] toCheck = lPass.getPassword();
            String userF = lUser.getText();
            String passF = new String(toCheck);

            if (userF.isEmpty() || passF.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials!", "Login Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String selectQuery = "SELECT * FROM credentials WHERE BINARY Users = ? AND BINARY Password = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                    statement.setString(1, userF);
                    statement.setString(2, passF);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(contentPanel, "Login successful!", "Success!", JOptionPane.INFORMATION_MESSAGE);

                        dispose();
                        new MainPanel(userF); //--After successful login--//

                    } else {
                        JOptionPane.showMessageDialog(contentPanel, "Invalid Username or Password!", "Login Error!", JOptionPane.WARNING_MESSAGE);
                        lPass.setText("");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage(), "Database Error.", JOptionPane.ERROR_MESSAGE);
            }
            Arrays.fill(toCheck, '\0');
        });
        JLabel lLabelLink = new JLabel("Don't have an account? Sign up here!");
        lLabelLink.setForeground(Color.WHITE);
        lLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Sign Up");

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
        loginP.add(lLabel);
        loginP.add(lUser);
        loginP.add(lPass);
        loginP.add(lButton);
        loginP.add(lLabelLink);

        //##################################### L O G I N C O D E S #####################################

        ImageIcon icon = new ImageIcon("src/main/resources/icons/messengerwhiteflip.png");

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
        setResizable(false);
        setLocation(500, 150);
        setSize(300, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void textFieldDesigner(JTextField textField) {
        textField.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth: 2; borderColor: #1877F2;");

        textField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        textField.setBackground(panelBg);
    }

    private void passwordFieldDesigner(JPasswordField passwordField) {
        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton: true; showCapsLock: true; borderWidth: 2; borderColor: #1877F2;");

        passwordField.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        passwordField.setBackground(panelBg);
    }

    private void buttonDesigner(JButton button) {
        button.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 2; borderColor: #1877F2;");

        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        button.setBackground(panelBg);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("uca.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
//        ModifiedFlatMacDarkLaf.setup();
        FlatMacDarkLaf.setup();
        new Login();
    }
}

//Call after successful login
class MainPanel extends JFrame {
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