public class Robot {
    private int id;
    private int x, y;
    private boolean carryingWater;
    private boolean carryingSurvivor;
    private int energy;
    private boolean active;

    public Robot(int id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.carryingWater = true;
        this.carryingSurvivor = false;
        this.energy = 3; // Energy lasts for 3 seconds of activity
        this.active = true;
    }

    public void move(int targetX, int targetY) {
        if (!active) return;
        this.x = targetX;
        this.y = targetY;
        energy--;
        if (energy <= 0) {
            active = false;
        }
    }

    public void recharge() {
        energy = 3;
        active = true;
    }

    public void deployWater() {
        if (carryingWater) {
            carryingWater = false;
        }
    }

    public void pickUpSurvivor() {
        carryingSurvivor = true;
    }

    public void dropSurvivor() {
        carryingSurvivor = false;
    }

    public boolean isActive() {
        return active;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCarryingWater() {
        return carryingWater;
    }

    public boolean isCarryingSurvivor() {
        return carryingSurvivor;
    }
}