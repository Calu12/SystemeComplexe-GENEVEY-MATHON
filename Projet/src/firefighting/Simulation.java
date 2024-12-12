package firefighting;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Robot> robots;
    private List<Fire> fires;
    private BaseStation baseStation;

    public Simulation(int numRobots, int baseX, int baseY) {
        robots = new ArrayList<>();
        fires = new ArrayList<>();
        baseStation = new BaseStation(baseX, baseY);

        for (int i = 0; i < numRobots; i++) {
            robots.add(new Robot(i, baseX, baseY));
        }
    }

    public Simulation(int numRobots){
        this(numRobots,0,0);
    }

    public void addFire(int x, int y) {
        fires.add(new Fire(x,y));
    }

    public void step() {
        for (Robot robot : robots) {
            if (!robot.isBusy() && !fires.isEmpty()) {
                Fire closestFire = robot.findClosestFire(fires);
                if (closestFire != null) {
                    robot.moveTo(closestFire);
                }
            }
            robot.act(fires);
        }
    }

    public void printStatus() {
        System.out.println("=== Simulation Status ===");
        for (Robot robot : robots) {
            System.out.println(robot);
        }
        for (Fire fire : fires) {
            System.out.println(fire);
        }
    }

    public void runSimulationStep() {
        System.out.println("=== Simulation Step ===");

        for (Robot robot : robots) {
            robot.decideMove(fires, robots, baseStation);
        }

        for (Robot robot : robots) {
            fires.removeIf(fire -> {
                if (robot.getX() == fire.getX() && robot.getY() == fire.getY()) {
                    System.out.println("Robot " + robot + " extinguished fire at (" + fire.getX() + ", " + fire.getY() + ").");
                    return fire.isExtinguished();
                }
                return false;
            });
        }

        showStatus();
    }

    public void showStatus() {
        System.out.println("=== Simulation Status ===");
        System.out.println("Base Station: " + baseStation);
        robots.forEach(System.out::println);
        fires.forEach(System.out::println);
    }
}
