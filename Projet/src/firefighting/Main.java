package firefighting;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation(2);

        // Ajouter des incendies
        simulation.addFire(5, 5);
        simulation.addFire(8, 8);
        simulation.addFire(2, 3);

        // Lancer plusieurs étapes de simulation
        for (int i = 0; i < 10; i++) { // 10 itérations de simulation
            System.out.println("=== Simulation Step ===");
            simulation.step();
            simulation.printStatus();
        }
    }
}
