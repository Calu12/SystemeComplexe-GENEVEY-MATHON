package firefighting;

import java.util.List;

public class Robot {
    private int id;
    private int x, y; // Position actuelle
    private boolean busy;

    public Robot(int id) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.busy = false;
    }

    public Robot(int id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
    }

    public boolean isBusy() {
        return busy;
    }
    public void moveTo(Fire fire) {
        System.out.println("Robot " + id + " moved to (" + fire.getX() + ", " + fire.getY() + ").");
        this.x = fire.getX();
        this.y = fire.getY();
        this.busy = true;
    }
    public void act(List<Fire> fires) {
        fires.removeIf(fire -> fire.getX() == x && fire.getY() == y);
        if (busy) {
            System.out.println("Robot " + id + " extinguished fire at (" + x + ", " + y + ").");
            busy = false;
        }
    }

    public void moveTowards(int targetX, int targetY) {
            if (x < targetX) x++;
            else if (x > targetX) x--;

            if (y < targetY) y++;
            else if (y > targetY) y--;

            System.out.println("Robot " + id + " moved to (" + x + ", " + y + ").");
    }

    public void decideMove(List<Fire> fires, List<Robot> robots, BaseStation base) {
        Fire targetFire = findClosestFire(fires);
        if (targetFire != null) {
            moveTowards(targetFire.getX(), targetFire.getY());
        } else {
            randomMove(); // Déplacement aléatoire si aucune cible
        }
    }

    public Fire findClosestFire(List<Fire> fires) {
        Fire closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Fire fire : fires) {
            double distance = Math.sqrt(Math.pow(fire.getX() - x, 2) + Math.pow(fire.getY() - y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closest = fire;
            }
        }

        return closest;
    }

    private void randomMove() {
        x += (Math.random() > 0.5 ? 1 : -1);
        y += (Math.random() > 0.5 ? 1 : -1);
        System.out.println("Robot " + id + " moved randomly to (" + x + ", " + y + ").");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
