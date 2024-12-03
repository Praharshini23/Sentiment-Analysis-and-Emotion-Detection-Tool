import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SentimentAnalysisGUI {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sentimentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sentiment Analysis");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(135, 206, 235));
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel instructionLabel = new JLabel("Enter text for analysis (min 3 letters):");
        instructionLabel.setBounds(50, 10, 300, 25);
        instructionLabel.setForeground(Color.BLACK);
        panel.add(instructionLabel);

        JTextField inputField = new JTextField();
        inputField.setBounds(50, 40, 500, 50);
        inputField.setBackground(Color.YELLOW);
        panel.add(inputField);

        JLabel charCountLabel = new JLabel("Characters: 0");
        charCountLabel.setBounds(50, 95, 150, 25);
        charCountLabel.setForeground(Color.BLACK);
        panel.add(charCountLabel);

        inputField.addCaretListener(e -> {
            int charCount = inputField.getText().length();
            charCountLabel.setText("Characters: " + charCount);
        });

        JButton analyzeButton = new JButton("Analyze Sentiment");
        analyzeButton.setBounds(50, 130, 200, 30);
        panel.add(analyzeButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(300, 130, 150, 30);
        panel.add(clearButton);

        JTextArea resultArea = new JTextArea();
        resultArea.setBounds(50, 180, 500, 120);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBackground(Color.YELLOW);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(50, 180, 500, 120);
        panel.add(scrollPane);

        JLabel sentimentLabel = new JLabel("Sentiment: ");
        sentimentLabel.setBounds(50, 310, 300, 30);
        sentimentLabel.setForeground(Color.BLACK);
        panel.add(sentimentLabel);

        analyzeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText();
                
                if (userInput.length() < 3) {
                    JOptionPane.showMessageDialog(panel, "Please enter at least 3 characters.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    ProcessBuilder pb = new ProcessBuilder("python3", "C:/Users/Praharshini/Desktop/sentiment_analysis.py", userInput);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String sentimentResult = in.readLine();

                    resultArea.setText("Sentiment Result: " + sentimentResult);
                    sentimentLabel.setText("Sentiment: " + sentimentResult);

                    // Store the result in the database
                    saveSentimentResult(userInput, sentimentResult);

                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error during sentiment analysis!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputField.setText("");
                resultArea.setText("");
                sentimentLabel.setText("Sentiment: ");
                charCountLabel.setText("Characters: 0");
            }
        });
    }

    private static void saveSentimentResult(String input_text, String sentiment) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO SentimentResults (input_text, sentiment) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, input_text);
            statement.setString(2, sentiment);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Result saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
