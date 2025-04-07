import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class login extends JFrame {
    private JPanel contentPanel;
    private JPanel signupP;
    private JPanel loginP;
//    private JPanel mainP;

    public login() {
        setTitle("Sign Up");
        String url = "jdbc:mysql://localhost:3306/infoman";
        String user = "root";
        String password = "";

        contentPanel = new JPanel();
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setSize(700, 300);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //########################## S I G N U P C O D E S ######################################

        signupP = new JPanel(new GridLayout(7, 1, 15, 15));
        signupP.setSize(300, 500);
        signupP.setBackground(Color.DARK_GRAY);
        signupP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel sLabel = new JLabel("Sign Up", JLabel.CENTER);
        sLabel.setFont(new Font("Arial", Font.BOLD, 24));
        sLabel.setForeground(Color.ORANGE);
        JTextField sUser = new JTextField(20);
//        sUser.setText("  Username");
        sUser.setToolTipText("Username");
        sUser.setCaretColor(Color.ORANGE);
        sUser.setCaretPosition(sUser.getText().length());
        JPasswordField sPass = new JPasswordField();
//        sPass.setText("  Password");
        sPass.setToolTipText("Password");
        sPass.setCaretColor(Color.ORANGE);
//        sPass.setCaretPosition(sPass.getText().length());
        JTextField sEmail = new JTextField(20);
//        sEmail.setText("  youremail@example.com");
//        sEmail.setCaretPosition(sEmail.getText().length());
        sEmail.setToolTipText("Email");
        sEmail.setCaretColor(Color.ORANGE);
        setLineBorder(sUser);
        setLineBorder(sPass);
        setLineBorder(sEmail);
        JButton sButton = new JButton("Sign Up");
        sButton.setForeground(Color.ORANGE);
        sButton.setBackground(Color.WHITE);
        sButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        sButton.setFocusable(false);
        sButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sButton.setBackground(Color.ORANGE);
                sButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sButton.setBackground(Color.WHITE);
                sButton.setForeground(Color.ORANGE);
            }

        });
        sButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userF = sUser.getText();
                String passF = sPass.getText();
                String emailF = sEmail.getText();

                if (userF.isEmpty() || passF.isEmpty() || emailF.isEmpty()) {
                    JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials");
                    return;
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String selectQuery = "SELECT * FROM credentials WHERE Users = ?";
                    try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                        statement.setString(1, userF);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(contentPanel, "Username already exists", "Username Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage());
                    ex.printStackTrace();
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String insertQuery = "INSERT INTO credentials (Users, Password, Email) VALUES (?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                        statement.setString(1, userF);
                        statement.setString(2, passF);
                        statement.setString(3, emailF);
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(contentPanel, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            setTitle("Log in");
                            remove(contentPanel);
                            contentPanel = loginP;
                            add(contentPanel, BorderLayout.CENTER);
                            revalidate();
                            repaint();
                        } else {
                            JOptionPane.showMessageDialog(contentPanel, "Signup failed.", "Signup Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        JLabel sLabelLink = new JLabel("Log in here!");
        sLabelLink.setForeground(Color.ORANGE);
        sLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Login");
                    remove(contentPanel);
                    contentPanel = loginP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sLabelLink.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sLabelLink.setForeground(Color.ORANGE);
            }
        });
        JLabel sLabel2 = new JLabel("Already have an account?");
        sLabel2.setForeground(Color.ORANGE);
        signupP.add(sLabel);
        signupP.add(sUser);
        signupP.add(sEmail);
        signupP.add(sPass);
        signupP.add(sButton);
        signupP.add(sLabel2);
        signupP.add(sLabelLink);

        //########################## S I G N U P C O D E S ######################################

        //##################################### L O G I N C O D E S ######################################

        loginP = new JPanel(new GridLayout(6, 1,15 ,15));
        loginP.setBackground(Color.DARK_GRAY);
        loginP.setSize(300, 500);
        loginP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lLabel = new JLabel("Login", JLabel.CENTER);
        lLabel.setFont(new Font("Arial", Font.BOLD, 24));
        lLabel.setForeground(Color.ORANGE);
        JTextField lUser = new JTextField(20);
//        lUser.setText("  Username or Email");
        lUser.setToolTipText("Username or Email");
        lUser.setCaretColor(Color.ORANGE);
//        lUser.setCaretPosition(lUser.getText().length());
        JPasswordField lPass = new JPasswordField();
        lPass.setToolTipText("Password");
//        lPass.setText("  Password");
        lPass.setCaretColor(Color.ORANGE);
//        lPass.setCaretPosition(lPass.getText().length());
        setLineBorder(lUser);
        setLineBorder(lPass);
        JButton lButton = new JButton("Login");
        lButton.setForeground(Color.ORANGE);
        lButton.setBackground(Color.WHITE);
        lButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        lButton.setFocusable(false);
        lButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lButton.setBackground(Color.ORANGE);
                lButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lButton.setBackground(Color.WHITE);
                lButton.setForeground(Color.ORANGE);
            }
        });
        lButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userF = lUser.getText();
                String passF = lPass.getText();

                if (userF.isEmpty() || passF.isEmpty()) {
                    JOptionPane.showMessageDialog(contentPanel, "Please enter a valid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String selectQuery = "SELECT * FROM credentials WHERE Users = ? AND Password = ?";
                    try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                        statement.setString(1, userF);
                        statement.setString(2, passF);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(contentPanel, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                            dispose();
                            new MainPanel(userF); //--After successful login--//

//                            setTitle("Main Panel");
//                            remove(contentPanel);
//                            contentPanel = mainP;
//                            add(contentPanel, BorderLayout.CENTER);
//                            revalidate();
//                            repaint();

                        } else {
                            JOptionPane.showMessageDialog(contentPanel, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(contentPanel, "Database error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        JLabel lLabelLink = new JLabel("Sign up here!");
        lLabelLink.setForeground(Color.ORANGE);
        lLabelLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lLabelLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setTitle("Sign Up");
                    remove(contentPanel);
                    contentPanel = signupP;
                    add(contentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lLabelLink.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lLabelLink.setForeground(Color.ORANGE);
            }
        });
        JLabel lLabel2 = new JLabel("Don't have an account?");
        lLabel2.setForeground(Color.ORANGE);
        loginP.add(lLabel);
        loginP.add(lUser);
        loginP.add(lPass);
        loginP.add(lButton);
        loginP.add(lLabel2);
        loginP.add(lLabelLink);

        //##################################### L O G I N C O D E S ######################################

        //##################################### M A I N P A N E L C O D E S ######################################

//        mainP = new JPanel(new GridLayout(3, 1));
//        mainP.setForeground(Color.BLACK);
//        mainP.setSize(300, 500);
//        mainP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        JLabel mLabel = new JLabel("Main Panel");
//        JButton mButton = new JButton("Logout");
//        mButton.setSize(100, 50);
//        mButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    JOptionPane.showMessageDialog(contentPanel, "You are logged out!", "Logout", JOptionPane.INFORMATION_MESSAGE);
//                    setTitle("Login");
//                    remove(contentPanel);
//                    contentPanel = loginP;
//                    add(contentPanel, BorderLayout.CENTER);
//                    revalidate();
//                    repaint();
//                } catch (Exception ex) {
//                    //
//                }
//            }
//        });
//        JLabel mLabel2 = new JLabel("You are logged in!");
//        mainP.add(mLabel);
//        mainP.add(mLabel2);
//        mainP.add(mButton);

        //##################################### M A I N P A N E L C O D E S ######################################

        //Some tricks
        contentPanel.add(signupP);
        remove(contentPanel);
        contentPanel = signupP;

        add(contentPanel, BorderLayout.CENTER);
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setLocation(500, 150);
        setSize(300, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setLineBorder(JTextField textField) {
        textField.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
    }

    public static void main(String[] args) {
        new login();
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

        JLabel usernameLabel = new JLabel("@" + username, JLabel.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to the Main Panel", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusable(false);
        logoutButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Color.ORANGE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(50, 50));
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "You are logged out!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new login();
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