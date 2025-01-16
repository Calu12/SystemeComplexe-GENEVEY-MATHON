package firefighting;

// Battery.java
public class Battery {
    private static final int MAX_CHARGE = 3000; // 3 seconds in milliseconds
    private static final int CHARGING_TIME = 2000; // 2 seconds in milliseconds
    private int currentCharge;
    private boolean isCharging;
    private long chargingStartTime;

    public Battery() {
        this.currentCharge = MAX_CHARGE;
        this.isCharging = false;
    }

    public boolean needsCharging() {
        return currentCharge <= 0;
    }

    public void startCharging() {
        isCharging = true;
        chargingStartTime = System.currentTimeMillis();
    }

    public boolean updateChargingStatus() {
        if (isCharging) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - chargingStartTime >= CHARGING_TIME) {
                currentCharge = MAX_CHARGE;
                isCharging = false;
                return true;
            }
        }
        return false;
    }

    public void consume(int amount) {
        currentCharge = Math.max(0, currentCharge - amount);
    }

    public boolean isCharging() {
        return isCharging;
    }
}