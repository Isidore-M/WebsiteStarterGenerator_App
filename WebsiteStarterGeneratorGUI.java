import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class WebsiteStarterGeneratorGUI extends JFrame {

    private JTextField nameField, domainField;
    private JComboBox<String> vibeDropdown;
    private JCheckBox faviconCheck, googleFontsCheck;
    private JSpinner fontCountSpinner;
    private JTextArea fontLinksArea;
    private JPanel fontOptionsPanel;
    private JButton generateButton;
    private JLabel statusLabel;

    private final Random rng = new Random();

    // Vibe -> List of palettes (each palette = 5 hex colors: primary, secondary, accent, surface, surfaceAlt)
    private final Map<String, List<String[]>> vibePalettes = new HashMap<>();

    public WebsiteStarterGeneratorGUI() {
        setTitle("Website Starter Generator 🚀");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 700);
        setLocationRelativeTo(null);

        // ---- palettes ----
        vibePalettes.put("Corporate", Arrays.asList(
                new String[]{"#0D1B2A", "#1B263B", "#415A77", "#F6F7FB", "#E9EDF5"},
                new String[]{"#0A2540", "#1F3B5C", "#2E5A88", "#F7F9FC", "#E9EFF7"}
        ));
        vibePalettes.put("Vibrant", Arrays.asList(
                new String[]{"#FF595E", "#FFCA3A", "#8AC926", "#FFFFFF", "#F6F6F6"},
                new String[]{"#ED1C24", "#FF9F1C", "#2EC4B6", "#FFFFFF", "#FAFAFA"}
        ));
        vibePalettes.put("Warm", Arrays.asList(
                new String[]{"#B56576", "#E56B6F", "#EAAC8B", "#FFF7F3", "#FCEADF"},
                new String[]{"#8D5524", "#C68642", "#E0AC69", "#FFF8F1", "#F7E5D1"}
        ));
        vibePalettes.put("Cool", Arrays.asList(
                new String[]{"#05668D", "#028090", "#00A896", "#F4FFFD", "#E9FBF8"},
                new String[]{"#1B4965", "#2C7DA0", "#5FA8D3", "#F7FBFF", "#EDF6FF"}
        ));
        vibePalettes.put("Fun", Arrays.asList(
                new String[]{"#F72585", "#B5179E", "#7209B7", "#FFF5FB", "#FCE8FF"},
                new String[]{"#FF477E", "#FF5C8A", "#00BBF9", "#FFF7FB", "#EAFBFF"}
        ));

        // ---- UI ----
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        main.setBackground(new Color(245, 245, 245));

        main.add(section("Project Info", buildProjectPanel()));
        main.add(Box.createVerticalStrut(12));
        main.add(section("Design Options", buildDesignPanel()));
        main.add(Box.createVerticalStrut(12));
        main.add(section("Fonts", buildFontsPanel()));
        main.add(Box.createVerticalStrut(12));
        main.add(section("Extras", buildExtrasPanel()));
        main.add(Box.createVerticalStrut(16));

        generateButton = new JButton("Generate Project");
        generateButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        generateButton.setBackground(new Color(0, 71, 171));
        generateButton.setForeground(Color.white);
        generateButton.setFocusPainted(false);
        generateButton.setBorderPainted(false);
        generateButton.setOpaque(true);
        generateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.addActionListener(this::onGenerate);
        main.add(generateButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(90, 90, 90));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        main.add(statusLabel);

        setContentPane(new JScrollPane(main,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    }

    private JPanel section(String title, JPanel content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(60, 60, 60)));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildProjectPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Website Name:"), c);
        nameField = new JTextField();
        c.gridx = 1; c.gridy = 0; c.weightx = 1; p.add(nameField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0; p.add(new JLabel("Domain / Field:"), c);
        domainField = new JTextField();
        c.gridx = 1; c.gridy = 1; c.weightx = 1; p.add(domainField, c);

        return p;
    }

    private JPanel buildDesignPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Vibe:"), c);
        vibeDropdown = new JComboBox<>(new String[]{"Corporate", "Vibrant", "Warm", "Cool", "Fun"});
        c.gridx = 1; c.gridy = 0; c.weightx = 1; p.add(vibeDropdown, c);

        return p;
    }

    private JPanel buildFontsPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        googleFontsCheck = new JCheckBox("Use Google Fonts?");
        googleFontsCheck.setOpaque(false);
        p.add(googleFontsCheck);

        fontOptionsPanel = new JPanel();
        fontOptionsPanel.setOpaque(false);
        fontOptionsPanel.setLayout(new BoxLayout(fontOptionsPanel, BoxLayout.Y_AXIS));
        fontOptionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; row.add(new JLabel("How many fonts?"), c);
        fontCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        c.gridx = 1; c.gridy = 0; c.weightx = 1; row.add(fontCountSpinner, c);
        fontOptionsPanel.add(row);

        fontOptionsPanel.add(new JLabel("Paste each Google Font <link> on a new line:"));
        fontLinksArea = new JTextArea(5, 30);
        fontLinksArea.setLineWrap(true);
        fontLinksArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(fontLinksArea);
        fontOptionsPanel.add(scroll);

        fontOptionsPanel.setVisible(false);
        p.add(fontOptionsPanel);

        googleFontsCheck.addActionListener(ae -> {
            boolean vis = googleFontsCheck.isSelected();
            fontOptionsPanel.setVisible(vis);
            fontOptionsPanel.revalidate();
        });

        return p;
    }

    private JPanel buildExtrasPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        faviconCheck = new JCheckBox("Include generated favicon");
        faviconCheck.setOpaque(false);
        p.add(faviconCheck);
        return p;
    }

    private void onGenerate(ActionEvent e) {
        String siteName = safe(nameField.getText());
        String domain = safe(domainField.getText());
        String vibe = (String) vibeDropdown.getSelectedItem();
        boolean useFonts = googleFontsCheck.isSelected();
        boolean includeFavicon = faviconCheck.isSelected();

        if (siteName.isEmpty() || domain.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Website Name and Domain/Field.", "Missing info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // collect Google font links (either full <link> tags or just hrefs)
        List<String> fontLinkTags = new ArrayList<>();
        if (useFonts) {
            int expected = (int) fontCountSpinner.getValue();
            List<String> lines = Arrays.stream(fontLinksArea.getText().split("\\R"))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
            if (lines.size() < expected) {
                JOptionPane.showMessageDialog(this,
                        "You selected " + expected + " font(s) but provided " + lines.size() + " link line(s).\n" +
                        "Either reduce the count or paste one <link> per line.",
                        "Google Fonts", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (int i = 0; i < expected; i++) {
                String entry = lines.get(i);
                if (entry.startsWith("<link")) {
                    fontLinkTags.add(entry);
                } else if (entry.startsWith("http")) {
                    fontLinkTags.add("<link href=\"" + entry + "\" rel=\"stylesheet\">");
                }
            }
        }

        generateButton.setEnabled(false);
        statusLabel.setText("Generating…");

        SwingUtilities.invokeLater(() -> {
            try {
                Path projectPath = resolveDesktopPath().resolve(siteName);
                if (Files.exists(projectPath)) {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Folder already exists:\n" + projectPath + "\nOverwrite files?",
                            "Folder exists", JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        statusLabel.setText("Cancelled.");
                        generateButton.setEnabled(true);
                        return;
                    }
                }

                // folders
                Files.createDirectories(projectPath.resolve("Assets/images"));
                Files.createDirectories(projectPath.resolve("Css"));
                Files.createDirectories(projectPath.resolve("Scripts"));

                // palette (random by vibe)
                String[] palette = pickPaletteFor(vibe);

                // files
                writeIndexHtml(projectPath, siteName, domain, vibe, fontLinkTags, includeFavicon);
                writeCss(projectPath, palette);
                writeJs(projectPath);
                writeThemePreview(projectPath, palette);

                if (includeFavicon) {
                    createFaviconPng(projectPath, siteName, palette[0]); // primary color
                }

                statusLabel.setText("Done: " + projectPath);
                JOptionPane.showMessageDialog(this, "Project created at:\n" + projectPath, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Generation failed", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error.");
            } finally {
                generateButton.setEnabled(true);
            }
        });
    }

    private Path resolveDesktopPath() {
        String home = System.getProperty("user.home");
        Path desktop = Paths.get(home, "Desktop");
        return Files.exists(desktop) ? desktop : Paths.get(home);
    }

    private String[] pickPaletteFor(String vibe) {
        List<String[]> list = vibePalettes.getOrDefault(vibe, vibePalettes.get("Corporate"));
        return list.get(rng.nextInt(list.size()));
    }

    private void writeIndexHtml(Path root, String siteName, String domain, String vibe,
                                List<String> fontLinks, boolean includeFavicon) throws IOException {
        StringBuilder head = new StringBuilder();
        for (String l : fontLinks) head.append("    ").append(l).append("\n");
        if (includeFavicon) {
            head.append("    <link rel=\"icon\" type=\"image/png\" href=\"Assets/images/favicon.png\">\n");
        }

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A %s website">
                %s    <title>%s</title>
                    <link rel="stylesheet" href="Css/app.css">
                </head>
                <body>
                    <header class="site-header">
                        <div class="container">
                            <h1 class="logo">%s</h1>
                            <nav class="nav">
                                <a href="#">Home</a>
                                <a href="#">About</a>
                                <a href="#">Services</a>
                                <a href="#">Contact</a>
                            </nav>
                        </div>
                    </header>

                    <main>
                        <section class="hero">
                            <div class="container">
                                <h2>Welcome to %s</h2>
                                <p>Your trusted source for %s — %s vibes.</p>
                                <a class="btn" href="#">Get Started</a>
                            </div>
                        </section>

                        <section class="features">
                            <div class="container grid">
                                <div class="card">
                                    <h3>Fast setup</h3>
                                    <p>Clean structure with HTML, CSS, JS and assets folders ready.</p>
                                </div>
                                <div class="card">
                                    <h3>Responsive</h3>
                                    <p>Mobile-first boilerplate and sensible defaults out of the box.</p>
                                </div>
                                <div class="card">
                                    <h3>Themeable</h3>
                                    <p>Change colors via CSS variables in one place.</p>
                                </div>
                            </div>
                        </section>
                    </main>

                    <footer class="site-footer">
                        <div class="container">
                            <p>&copy; %d %s. All rights reserved.</p>
                        </div>
                    </footer>

                    <script src="Scripts/app.js"></script>
                </body>
                </html>
                """.formatted(escape(domain), head.toString(), escape(siteName),
                escape(siteName), escape(siteName), escape(domain), escape(vibe),
                Calendar.getInstance().get(Calendar.YEAR), escape(siteName));

        Files.writeString(root.resolve("index.html"), html, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void writeCss(Path root, String[] palette) throws IOException {
        // palette indices: 0 primary, 1 secondary, 2 accent, 3 surface, 4 surfaceAlt
        String textColor = preferDarkText(palette[3]) ? "#111111" : "#FFFFFF";

        String css = """
                :root{
                  --primary:%s;
                  --secondary:%s;
                  --accent:%s;
                  --surface:%s;
                  --surface-alt:%s;
                  --text:%s;
                }
                *{box-sizing:border-box}
                html,body{margin:0;padding:0}
                body{
                  font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
                  background: var(--surface);
                  color: var(--text);
                  line-height:1.6;
                }
                .container{
                  width:min(1100px, 92%%);
                  margin-inline:auto;
                  padding: 1rem;
                }
                .site-header{
                  background: var(--primary);
                  color:#fff;
                }
                .site-header .logo{margin:0;font-size:1.4rem}
                .nav{display:flex;gap:1rem;flex-wrap:wrap}
                .nav a{color:#fff;text-decoration:none;opacity:.95}
                .nav a:hover{opacity:1;text-decoration:underline}
                
                .hero{
                  background: linear-gradient(135deg, var(--secondary), var(--accent));
                  color:#fff;
                  padding: clamp(2rem, 6vw, 4rem) 0;
                  text-align:center;
                }
                .btn{
                  display:inline-block;
                  padding:.75rem 1rem;
                  background:#fff;
                  color:#000;
                  border-radius:.5rem;
                  text-decoration:none;
                  font-weight:600;
                  margin-top:1rem;
                }
                
                .features .grid{
                  display:grid;
                  gap:1rem;
                  grid-template-columns: repeat(3, 1fr);
                }
                .card{
                  background: var(--surface-alt);
                  padding:1rem;
                  border-radius:.8rem;
                  box-shadow: 0 2px 8px rgba(0,0,0,.06);
                }
                
                .site-footer{
                  background: var(--primary);
                  color:#fff;
                  margin-top:2rem;
                }
                
                /* Responsive */
                @media (max-width: 900px){
                  .features .grid{grid-template-columns: 1fr 1fr;}
                }
                @media (max-width: 600px){
                  .nav{justify-content:center}
                  .features .grid{grid-template-columns: 1fr;}
                }
                """.formatted(palette[0], palette[1], palette[2], palette[3], palette[4], textColor);

        Files.writeString(root.resolve("Css/app.css"), css, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void writeJs(Path root) throws IOException {
        String js = "console.log('Website loaded. Starter template ready.');";
        Files.writeString(root.resolve("Scripts/app.js"), js, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void writeThemePreview(Path root, String[] palette) throws IOException {
        String html = """
                <!doctype html>
                <html lang="en"><head><meta charset="utf-8">
                <title>Theme Preview</title>
                <style>
                  body{font-family:Arial, sans-serif;margin:0}
                  .swatch{height:90px;display:flex;align-items:center;justify-content:center;color:#fff;font-weight:700}
                </style>
                </head><body>
                  <div class="swatch" style="background:%s">--primary: %s</div>
                  <div class="swatch" style="background:%s">--secondary: %s</div>
                  <div class="swatch" style="background:%s">--accent: %s</div>
                  <div class="swatch" style="background:%s">--surface: %s</div>
                  <div class="swatch" style="background:%s">--surface-alt: %s</div>
                </body></html>
                """.formatted(palette[0], palette[0], palette[1], palette[1], palette[2], palette[2],
                palette[3], palette[3], palette[4], palette[4]);
        Files.writeString(root.resolve("theme-preview.html"), html, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void createFaviconPng(Path root, String siteName, String hexBg) throws IOException {
        int size = 128;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.decode(hexBg));
        g.fillRoundRect(0, 0, size, size, 28, 28);

        String letter = siteName.trim().isEmpty() ? "W" : siteName.substring(0, 1).toUpperCase();
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("SansSerif", Font.BOLD, 84));
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(letter)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(letter, x, y);
        g.dispose();

        Path out = root.resolve("Assets/images/favicon.png");
        ImageIO.write(img, "png", out.toFile());
    }

    // ---- helpers ----
    private static String safe(String s){ return s == null ? "" : s.trim(); }

    private static String escape(String s){
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    private static boolean preferDarkText(String hex){
        Color c = Color.decode(hex);
        // perceived luminance (sRGB)
        double lum = 0.2126*(c.getRed()/255.0) + 0.7152*(c.getGreen()/255.0) + 0.0722*(c.getBlue()/255.0);
        return lum > 0.6; // if surface is light, use dark text
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
        SwingUtilities.invokeLater(() -> new WebsiteStarterGeneratorGUI().setVisible(true));
    }
}