import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Represents a trained FAQ Intent with associated keywords and responses.
 */
class FAQIntent {
    String category;
    List<String> keywords;
    List<String> responses;

    public FAQIntent(String category, List<String> keywords, List<String> responses) {
        this.category = category;
        this.keywords = keywords;
        this.responses = responses;
    }
}

/**
 * NLP Engine for tokenization, normalization, and intent matching.
 */
class ChatbotEngine {
    private final List<FAQIntent> knowledgeBase;
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "are", "am", "in", "to", "of", "and", "or", "for", "on", "with", "at", "by", "how", "what", "can", "i", "do"
    ));

    public ChatbotEngine() {
        knowledgeBase = new ArrayList();
        trainBot();
    }

    private void trainBot() {
        // Training the bot with FAQ categories, keywords, and responses
        knowledgeBase.add(new FAQIntent(
            "GREETING",
            Arrays.asList("hi", "hello", "hey", "greetings", "morning", "evening"),
            Arrays.asList("Hello! How can I assist you today?", "Hi there! What can I help you with?", "Greetings! Ask me anything about our services.")
        ));

        knowledgeBase.add(new FAQIntent(
            "HOURS",
            Arrays.asList("hours", "timing", "timings", "open", "close", "time", "working"),
            Arrays.asList("Our support desk is open Monday to Saturday from 9:00 AM to 7:00 PM IST.", "We operate between 9:00 AM and 7:00 PM (Mon-Sat).")
        ));

        knowledgeBase.add(new FAQIntent(
            "SERVICES",
            Arrays.asList("services", "offer", "provide", "do", "features", "products"),
            Arrays.asList("We offer software development, cloud hosting, AI integration, and 24/7 technical support.", "Our core services include custom web/app development and IT consulting.")
        ));

        knowledgeBase.add(new FAQIntent(
            "PRICING",
            Arrays.asList("price", "cost", "pricing", "charges", "fees", "plan", "subscription"),
            Arrays.asList("Our pricing plans start at ₹2,500/month for standard tiers. Custom enterprise solutions are quoted upon request.", "We offer flexible pricing starting from ₹2,500 per month.")
        ));

        knowledgeBase.add(new FAQIntent(
            "CONTACT",
            Arrays.asList("contact", "support", "email", "phone", "call", "reach", "help"),
            Arrays.asList("You can reach our support team at support@example.com or call us toll-free at 1800-123-4567.", "Need direct help? Email us at support@example.com.")
        ));
        
        knowledgeBase.add(new FAQIntent(
            "FAREWELL",
            Arrays.asList("bye", "goodbye", "thanks", "thank", "exit", "quit"),
            Arrays.asList("Goodbye! Have a wonderful day ahead.", "Thank you for chatting with us. Feel free to return if you have more questions!")
        ));
    }

    /**
     * NLP Preprocessing: Tokenizes text, removes punctuation, and filters stopwords.
     */
    public List<String> tokenizeAndClean(String input) {
        if (input == null || input.isEmpty()) return new ArrayList();
        
        // Normalize: lowercase and remove punctuation
        String cleaned = input.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        String[] tokens = cleaned.split("\\s+");
        
        List<String> filteredTokens = new ArrayList();
        for (String token : tokens) {
            if (!token.isEmpty() && !STOPWORDS.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    /**
     * Matches user query tokens against trained intents using a scoring metric.
     */
    public String getResponse(String userInput) {
        List<String> userTokens = tokenizeAndClean(userInput);
        if (userTokens.isEmpty()) {
            return "Could you please rephrase your question with more specific keywords?";
        }

        FAQIntent bestMatch = null;
        int maxScore = 0;

        for (FAQIntent intent : knowledgeBase) {
            int score = 0;
            for (String keyword : intent.keywords) {
                if (userTokens.contains(keyword)) {
                    score++;
                }
            }
            if (score > maxScore) {
                maxScore = score;
                bestMatch = intent;
            }
        }

        if (maxScore > 0 && bestMatch != null) {
            Random rand = new Random();
            List<String> responses = bestMatch.responses;
            return responses.get(rand.nextInt(responses.size()));
        }

        return "I'm sorry, I didn't quite catch that. You can ask me about our working hours, pricing, services, or contact info.";
    }
}

/**
 * Java Swing GUI for real-time interaction.
 */
public class JavaChatbot extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private ChatbotEngine botEngine;

    public JavaChatbot() {
        botEngine = new ChatbotEngine();

        setTitle("AI FAQ Assistant");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout Components
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Welcome Message
        appendMessage("Bot", "Hello! I am your virtual assistant. Ask me about our hours, services, pricing, or contact details.");

        // Event Listeners
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserMessage();
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
    }

    private void processUserMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        appendMessage("You", text);
        inputField.setText("");

        // Simulate thinking / instant retrieval
        String response = botEngine.getResponse(text);
        appendMessage("Bot", response);
    }

    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JavaChatbot().setVisible(true);
            }
        });
    }
}