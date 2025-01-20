import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UI {
    private Simulation simulation;
    private JFrame frame;
    private JPanel gridPanel;
    private JTextField robotField, fireField, survivorField;

    public UI(Simulation simulation) {
        this.simulation = simulation;
        frame = new JFrame("Firefighting Robot Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        JPanel settingsPanel = new JPanel(new GridLayout(5, 2));
        JLabel robotLabel = new JLabel("Number of Robots:");
        robotField = new JTextField("7");
        JLabel fireLabel = new JLabel("Number of Fires:");
        fireField = new JTextField("10");
        JLabel survivorLabel = new JLabel("Number of Survivors:");
        survivorField = new JTextField("10");

        JButton startButton = new JButton("Start Simulation");
        JButton stepButton = new JButton("Next Step");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int numRobots = Integer.parseInt(robotField.getText());
                int numFires = Integer.parseInt(fireField.getText());
                int numSurvivors = Integer.parseInt(survivorField.getText());
                simulation = new Simulation(numRobots, numFires, numSurvivors);
                renderGrid();
            }
        });

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.update();
                renderGrid();
            }
        });

        settingsPanel.add(robotLabel);
        settingsPanel.add(robotField);
        settingsPanel.add(fireLabel);
        settingsPanel.add(fireField);
        settingsPanel.add(survivorLabel);
        settingsPanel.add(survivorField);
        settingsPanel.add(startButton);
        settingsPanel.add(stepButton);

        // Panel principal qui contiendra la grille
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderGrid(g);
            }
        };
        gridPanel.setPreferredSize(new Dimension(600, 600));

        // Utilisation d'un JScrollPane pour permettre le défilement
        JScrollPane gridScrollPane = new JScrollPane(gridPanel);

        // Splitter pour avoir la grille et le panneau de settings côte à côte
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, settingsPanel, gridScrollPane);
        splitPane.setResizeWeight(0.2);  // 20% pour le panneau de settings et 80% pour la grille

        frame.add(splitPane, BorderLayout.CENTER);

        renderGrid();
    }

    private void renderGrid() {
        gridPanel.repaint();
    }

    private void renderGrid(Graphics g) {
        FireZone fireZone = simulation.getFireZone();
        Headquarters headquarters = simulation.getHeadquarters();
        List<Robot> robots = simulation.getRobots();
        int cellSize = gridPanel.getWidth() / 10; // Dynamique en fonction de la largeur du panneau

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int x = i * cellSize;
                int y = j * cellSize;

                // Dessiner le fond de chaque cellule
                if (headquarters.getX() == i && headquarters.getY() == j) {
                    g.setColor(Color.GREEN);
                } else if (fireZone.isOnFire(i, j)) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(x, y, cellSize, cellSize);

                // Dessiner les survivants
                if (fireZone.hasSurvivor(i, j)) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
                }

                // Dessiner les robots
                for (Robot robot : robots) {
                    if (robot.getX() == i && robot.getY() == j) {
                        g.setColor(robot.isCarryingSurvivor() ? Color.MAGENTA : Color.BLUE);
                        g.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
                        break;
                    }
                }

                // Dessiner les lignes de la grille
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public void launch() {
        frame.setSize(800, 600);  // Taille de la fenêtre, ajustée pour bien voir le panneau de settings
        frame.setVisible(true);
    }
}
