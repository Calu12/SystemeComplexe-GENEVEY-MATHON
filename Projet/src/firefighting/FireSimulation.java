package firefighting;
import java.util.Scanner;

public class FireSimulation {
    private static final int UPDATE_INTERVAL = 500; // milliseconds
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

        long lastUpdateTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
                grid.printGrid();
                grid.simulateStep();
                lastUpdateTime = currentTime;
            }
            
            if (System.in.available() > 0) {
                String input = scanner.nextLine();
                if (input.equals("q")) {
                    break;
                }
            }
        }
    }
}
