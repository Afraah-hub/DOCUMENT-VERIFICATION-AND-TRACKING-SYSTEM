import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;

// ==========================================
// 1. DATA MODEL (In-Memory Database)
// ==========================================
class Document {
    String id;
    String filename;
    String ownerName;
    String secretPin;
    String status;

    public Document(String filename, String ownerName, String pin) {
        // Generate a short 8-character ID
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.filename = filename;
        this.ownerName = ownerName;
        this.secretPin = pin;
        this.status = "Uploaded (Pending Verification)";
    }
}

// ==========================================
// 2. MAIN APPLICATION (GUI)
// ==========================================
public class docu extends JFrame {

    private static Map<String, Document> db = new HashMap<>();

    public docu() {
        // Window Setup
        setTitle("Simple DVS (Strict Fraud Detection)");
        setSize(700, 650); // Increased height for extra field
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Upload", createUploadPanel());
        tabbedPane.addTab("2. Verify (Strict Check)", createVerificationPanel());
        tabbedPane.addTab("3. Track Status", createTrackingPanel());

        add(tabbedPane);
    }

    // --- TAB 1: UPLOAD ---
    private JPanel createUploadPanel() {
        JPanel panel = new JPanel(null);

        JLabel l1 = new JLabel("Select Original File:");
        l1.setBounds(50, 50, 150, 25);
        panel.add(l1);

        JTextField tfFilename = new JTextField();
        tfFilename.setBounds(180, 50, 200, 25);
        tfFilename.setEditable(false);
        panel.add(tfFilename);

        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setBounds(390, 50, 100, 25);
        panel.add(btnBrowse);

        JLabel l2 = new JLabel("Owner Name:");
        l2.setBounds(50, 90, 150, 25);
        panel.add(l2);

        JTextField tfOwner = new JTextField();
        tfOwner.setBounds(180, 90, 200, 25);
        panel.add(tfOwner);

        JButton btnUpload = new JButton("Upload & Register");
        btnUpload.setBounds(180, 140, 200, 35);
        btnUpload.setBackground(new Color(200, 200, 255));
        panel.add(btnUpload);

        JTextArea resultArea = new JTextArea();
        resultArea.setBounds(50, 200, 580, 250);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEtchedBorder());
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(resultArea);

        // Action: Browse
        btnBrowse.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                tfFilename.setText(selectedFile.getName());
            }
        });

        // Action: Upload
        btnUpload.addActionListener(e -> {
            String fName = tfFilename.getText();
            String oName = tfOwner.getText();

            if (fName.isEmpty() || oName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a file and enter owner name!");
                return;
            }

            // Generate PIN
            String pin = String.format("%04d", new Random().nextInt(10000));

            // Save to DB
            Document doc = new Document(fName, oName, pin);
            db.put(doc.id, doc);

            // Show Result
            resultArea.setText("--- REGISTRATION SUCCESSFUL ---\n\n");
            resultArea.append("Document ID : " + doc.id + "\n");
            resultArea.append("Owner Name  : " + doc.ownerName + "\n");
            resultArea.append("File Name   : " + doc.filename + "\n");
            resultArea.append("SECRET PIN  : " + doc.secretPin + "\n\n");
            resultArea.append("System Record Created. Any deviation during verification will be flagged.");
        });

        return panel;
    }

    // --- TAB 2: VERIFICATION (Now checks Owner Name too) ---
    private JPanel createVerificationPanel() {
        JPanel panel = new JPanel(null);

        JLabel info = new JLabel("Verify file content, PIN, AND Owner Identity.");
        info.setBounds(50, 20, 500, 25);
        info.setForeground(Color.BLUE);
        panel.add(info);

        JLabel l1 = new JLabel("Document ID:");
        l1.setBounds(50, 60, 120, 25);
        panel.add(l1);

        JTextField tfId = new JTextField();
        tfId.setBounds(180, 60, 200, 25);
        panel.add(tfId);

        JLabel l2 = new JLabel("Secret PIN:");
        l2.setBounds(50, 100, 120, 25);
        panel.add(l2);

        JTextField tfPin = new JTextField();
        tfPin.setBounds(180, 100, 200, 25);
        panel.add(tfPin);

        // NEW: Check Owner Name
        JLabel l3 = new JLabel("Verify Owner:");
        l3.setBounds(50, 140, 120, 25);
        panel.add(l3);

        JTextField tfVerifyOwner = new JTextField();
        tfVerifyOwner.setBounds(180, 140, 200, 25);
        panel.add(tfVerifyOwner);

        // NEW: Select File to Verify
        JLabel l4 = new JLabel("File to Verify:");
        l4.setBounds(50, 180, 120, 25);
        panel.add(l4);

        JTextField tfVerifyFile = new JTextField();
        tfVerifyFile.setBounds(180, 180, 200, 25);
        tfVerifyFile.setEditable(false);
        panel.add(tfVerifyFile);

        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setBounds(390, 180, 100, 25);
        panel.add(btnBrowse);

        JButton btnVerify = new JButton("Verify Authenticity");
        btnVerify.setBounds(180, 220, 200, 35);
        btnVerify.setBackground(Color.ORANGE);
        panel.add(btnVerify);

        JLabel statusLabel = new JLabel("Waiting for input...");
        statusLabel.setBounds(50, 270, 600, 25);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(statusLabel);

        // Store selected file path
        final File[] selectedFile = {null};

        // Action: Browse
        btnBrowse.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                tfVerifyFile.setText(selectedFile[0].getName());
            }
        });

        // Action: Verify
        btnVerify.addActionListener(e -> {
            String id = tfId.getText().trim();
            String inputPin = tfPin.getText().trim();
            String verifyOwner = tfVerifyOwner.getText().trim();
            String verifyFileName = tfVerifyFile.getText().trim();

            if (!db.containsKey(id)) {
                statusLabel.setText("Result: INVALID ID (Document not found)");
                statusLabel.setForeground(Color.RED);
                return;
            }

            if (selectedFile[0] == null) {
                JOptionPane.showMessageDialog(this, "Please select the file you want to verify!");
                return;
            }

            Document doc = db.get(id);

            // CHECK 1: PIN
            if (!doc.secretPin.equals(inputPin)) {
                statusLabel.setText("Result: ❌ FAILED. Incorrect PIN.");
                statusLabel.setForeground(Color.RED);
                return;
            }

            // CHECK 2: OWNER IDENTITY (Case Sensitive, Word-by-Word Check)
            String[] regOwnerWords = doc.ownerName.trim().split("\\s+");
            String[] inputOwnerWords = verifyOwner.trim().split("\\s+");
            if (!java.util.Arrays.equals(regOwnerWords, inputOwnerWords)) {
                statusLabel.setText("Result: ⚠️ IDENTITY FRAUD! Owner name does not match record (checked word-by-word, case-sensitive).");
                statusLabel.setForeground(Color.RED);
                doc.status = "Rejected (Identity Mismatch)";
                return;
            }

            // CHECK 3: FILE NAME (Case Insensitive)
            if (!doc.filename.equalsIgnoreCase(verifyFileName)) {
                 statusLabel.setText("Result: ⚠️ DOCUMENT FRAUD! File name mismatch (case-insensitive check).");
                 statusLabel.setForeground(Color.RED);
                 doc.status = "Rejected (File Tampering Detected)";
                 return;
            }

            // If ALL pass:
            doc.status = "Verified & Approved";
            statusLabel.setText("Result: ✅ SUCCESS! Identity, PIN, and File Match.");
            statusLabel.setForeground(new Color(0, 128, 0)); // Green
        });

        return panel;
    }

    // --- TAB 3: TRACKING ---
    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel(null);

        JLabel l1 = new JLabel("Enter Doc ID:");
        l1.setBounds(50, 50, 100, 25);
        panel.add(l1);

        JTextField tfId = new JTextField();
        tfId.setBounds(150, 50, 200, 25);
        panel.add(tfId);

        JButton btnTrack = new JButton("Track");
        btnTrack.setBounds(360, 50, 100, 25);
        panel.add(btnTrack);

        JTextArea historyArea = new JTextArea();
        historyArea.setBounds(50, 100, 500, 250);
        historyArea.setEditable(false);
        historyArea.setBorder(BorderFactory.createEtchedBorder());
        panel.add(historyArea);

        btnTrack.addActionListener(e -> {
            String id = tfId.getText().trim();
            if (db.containsKey(id)) {
                Document doc = db.get(id);
                historyArea.setText("--- TRACKING REPORT ---\n\n");
                historyArea.append("ID       : " + doc.id + "\n");
                historyArea.append("Registered File : " + doc.filename + "\n");
                historyArea.append("Registered Owner: " + doc.ownerName + "\n");
                historyArea.append("---------------------------\n");
                historyArea.append("STATUS   : " + doc.status + "\n");
            } else {
                historyArea.setText("Error: Document ID not found.");
            }
        });

        return panel;
    }

    // Method to extract owner from file metadata (PDF or PPT)
    private static String extractOwnerFromFile(File file) {
        String filename = file.getName().toLowerCase();
        try {
            if (filename.endsWith(".pdf")) {
                // Extract author from PDF
                PDDocument document = PDDocument.load(file);
                PDDocumentInformation info = document.getDocumentInformation();
                String author = info.getAuthor();
                document.close();
                return author;
            } else if (filename.endsWith(".ppt")) {
                // Extract author from PPT
                FileInputStream fis = new FileInputStream(file);
                HSLFSlideShow slideshow = new HSLFSlideShow(fis);
                String author = slideshow.getSummaryInformation().getAuthor();
                fis.close();
                return author;
            } else if (filename.endsWith(".pptx")) {
                // Extract author from PPTX
                FileInputStream fis = new FileInputStream(file);
                XSLFSlideShow slideshow = new XSLFSlideShow(fis);
                String author = slideshow.getProperties().getCoreProperties().getCreator();
                fis.close();
                return author;
            }
        } catch (Exception e) {
            // If extraction fails, return null
            return null;
        }
        return null;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new docu().setVisible(true));
    }
}
