import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Robot> robots;
    private FireZone fireZone;
    private Headquarters headquarters;

    public Simulation() {
        this(7, 10, 10); // Default values
    }

    public Simulation(int numRobots, int initialFires, int initialSurvivors) {
        fireZone = new FireZone(10, 10, initialFires, initialSurvivors);
        headquarters = new Headquarters(0, 0);
        robots = new ArrayList<>();
        for (int i = 0; i < numRobots; i++) {
            robots.add(new Robot(i, headquarters.getX(), headquarters.getY()));
        }
    }

    public void update() {
        fireZone.spreadFire();
        for (Robot robot : robots) {
            if (!robot.isActive()) {
                robot.recharge();
                continue;
            }
    
            if (robot.isCarryingSurvivor()) {
                // Déplacement progressif vers le quartier général
                int targetX = headquarters.getX();
                int targetY = headquarters.getY();
                int dx = Integer.compare(targetX, robot.getX());
                int dy = Integer.compare(targetY, robot.getY());
                robot.move(robot.getX() + dx, robot.getY() + dy);
    
                // Déposer le survivant une fois au quartier général
                if (robot.getX() == headquarters.getX() && robot.getY() == headquarters.getY()) {
                    robot.dropSurvivor();
                }
            } else {
                boolean actionTaken = false;
    
                // Cherche un survivant adjacent
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = robot.getX() + dx;
                        int ny = robot.getY() + dy;
                        if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                            if (fireZone.hasSurvivor(nx, ny)) {
                                robot.move(nx, ny);
                                robot.pickUpSurvivor();
                                fireZone.removeSurvivor(nx, ny);
                                actionTaken = true;
                                break;
                            }
                        }
                    }
                    if (actionTaken) break;
                }
    
                // Cherche un feu adjacent à éteindre
                if (!actionTaken) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = robot.getX() + dx;
                            int ny = robot.getY() + dy;
                            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                                if (fireZone.isOnFire(nx, ny)) {
                                    robot.move(nx, ny);
                                    fireZone.extinguishFire(nx, ny);
                                    actionTaken = true;
                                    break;
                                }
                            }
                        }
                        if (actionTaken) break;
                    }
                }
    
                // Mouvement aléatoire si aucune action n'est prise
                if (!actionTaken) {
                    int randomX = Math.max(0, Math.min(9, robot.getX() + (int)(Math.random() * 3) - 1));
                    int randomY = Math.max(0, Math.min(9, robot.getY() + (int)(Math.random() * 3) - 1));
                    robot.move(randomX, randomY);
                }
            }
        }
    }
    

    public FireZone getFireZone() {
        return fireZone;
    }

    public Headquarters getHeadquarters() {
        return headquarters;
    }

    public List<Robot> getRobots() {
        return robots;
    }
}