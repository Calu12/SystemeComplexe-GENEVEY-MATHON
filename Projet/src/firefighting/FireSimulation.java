package firefighting;
import java.util.Scanner;

public class FireSimulation {
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

        while (true) {
            grid.printGrid();
            grid.simulateStep();
            System.out.println("Press Enter to simulate next step...");
            scanner.nextLine();
        }
    }
}
