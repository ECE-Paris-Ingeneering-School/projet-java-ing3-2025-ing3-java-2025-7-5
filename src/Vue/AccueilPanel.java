package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccueilPanel extends JPanel {
    private float opacity = 0f; // Pour l'effet de transition

    public AccueilPanel(MainFrame frame) {
        setOpaque(false); // Permet le fade-in
        setBackground(new Color(240, 248, 255)); // Bleu très clair

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Bienvenue sur Doctolibre");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 144, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridy++;
        JButton btnInscription = createStyledButton("S'inscrire", new Color(50, 205, 50));
        btnInscription.addActionListener(e -> frame.showPanel("inscription"));
        add(btnInscription, gbc);

        gbc.gridy++;
        JButton btnConnexion = createStyledButton("Se connecter", new Color(65, 105, 225));
        btnConnexion.addActionListener(e -> frame.showPanel("connexion"));
        add(btnConnexion, gbc);

        startFadeIn();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.white);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Color originalColor = color;
        Color hoverColor = color.brighter();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void startFadeIn() {
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                timer.stop();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (opacity > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g2);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }
}
