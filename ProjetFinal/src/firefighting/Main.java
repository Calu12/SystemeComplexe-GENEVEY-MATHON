public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        UI ui = new UI(simulation);
        ui.launch();
    }
}