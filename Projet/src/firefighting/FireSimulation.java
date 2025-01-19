package firefighting;
import java.util.Scanner;
import java.io.IOException;

// Cette classe gère la simulation principale de l'incendie et du comportement des robots.
public class FireSimulation {
    private static final int UPDATE_INTERVAL = 500; // milliseconds
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collecte des paramètres nécessaires pour initialiser la simulation.
        System.out.println("Enter grid size: ");
        int gridSize = scanner.nextInt();
        System.out.println("Enter fire spread probability (0.0 to 1.0): ");
        double fireSpreadProb = Double.parseDouble(scanner.next().replace(',', '.'));
        System.out.println("Enter burn time: ");
        int burnTime = scanner.nextInt();
        System.out.println("Enter tree percentage (0.0 to 1.0): ");
        double treePercentage = Double.parseDouble(scanner.next().replace(',', '.'));
        System.out.println("Enter number of initial fires: ");
        int initialFires = scanner.nextInt();
        System.out.println("Enter number of survivors: ");
        int numSurvivors = scanner.nextInt();
        System.out.println("Enter number of robots: ");
        int numRobots = scanner.nextInt();
        
        Grid grid = new Grid(gridSize, fireSpreadProb, burnTime, treePercentage, initialFires, numSurvivors, numRobots);

        // Initialisation du coordinateur de l'essaim.
        SwarmCoordinator coordinator = new SwarmCoordinator(new Robot[numRobots], grid);
        grid.setCoordinator(coordinator);

        // Initialisation des robots et ajout à la grille.
        Robot[] robots = new Robot[numRobots];
        for (int i = 0; i < numRobots; i++) {
            robots[i] = new Robot(i, 0, 0, grid, coordinator);
        }

        grid.setRobots(robots); // Enregistre les robots dans la grille.
        coordinator.setRobots(robots); // Associe les robots au coordinateur.
        
        long lastUpdateTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();

            // Exécute une étape de la simulation à intervalles réguliers.
            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
                grid.printGrid();
                grid.simulateStep();
                lastUpdateTime = currentTime;
            }

            // Gère l'interruption de la simulation par l'utilisateur. 
            try {
                if (System.in.available() > 0) {
                    String input = scanner.nextLine();
                    if (input.equals("q")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }
}

